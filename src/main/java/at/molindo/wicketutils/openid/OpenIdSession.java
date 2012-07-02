/**
 * Copyright 2010 Molindo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.molindo.wicketutils.openid;

import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.IClusterable;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Session;
import org.apache.wicket.WicketRuntimeException;
import org.openid4java.association.AssociationException;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.consumer.InMemoryNonceVerifier;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.MessageException;
import org.openid4java.message.MessageExtension;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;
import org.openid4java.message.sreg.SRegMessage;
import org.openid4java.message.sreg.SRegRequest;
import org.openid4java.message.sreg.SRegResponse;

import at.molindo.wicketutils.utils.WicketUtils;

public class OpenIdSession implements IClusterable {

	private static final MetaDataKey<ConsumerManager> CONSUMER_MANAGER_KEY = new MetaDataKey<ConsumerManager>() {
		private static final long serialVersionUID = 1L;
	};

	private static final long serialVersionUID = 1L;

	private DiscoveryInformation discoveryInformation;

	public static OpenIdSession get() {
		return getWebSession().getOpenIdSession();
	}

	public static IOpenIdWebSession getWebSession() {
		return (IOpenIdWebSession) Session.get();
	}

	protected String getOpenIdReturnUrl() {
		return WicketUtils.toAbsolutePath(OpenIdReturnPage.class);
	}

	public void processReturn(PageParameters params) {
		ParameterList response = new ParameterList(params);
		try {
			VerificationResult verificationResult = getConsumerManager().verify(getOpenIdReturnUrl(), response,
					discoveryInformation);
			Identifier verifiedIdentifier = verificationResult.getVerifiedId();
			if (verifiedIdentifier != null) {
				AuthSuccess authSuccess = (AuthSuccess) verificationResult.getAuthResponse();

				OpenIdDetails details = new OpenIdDetails();
				details.setOpenId(verifiedIdentifier.getIdentifier());

				// try to get additional details
				if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)) {
					MessageExtension ext = authSuccess.getExtension(AxMessage.OPENID_NS_AX);

					if (ext instanceof FetchResponse) {
						FetchResponse fetchResp = (FetchResponse) ext;

						details.setMail(fetchResp.getAttributeValue("email"));

						String fullname = fetchResp.getAttributeValue("fullname");
						if (fullname == null) {
							String firstname = fetchResp.getAttributeValue("firstname");
							String lastname = fetchResp.getAttributeValue("lastname");

							if (firstname == null) {
								fullname = lastname == null ? null : lastname;
							} else if (lastname != null) {
								fullname = firstname + " " + lastname;
							} else {
								fullname = firstname;
							}
						}
						details.setName(fullname);
					}
				} else if (authSuccess.hasExtension(SRegMessage.OPENID_NS_SREG)) {
					MessageExtension extension = authSuccess.getExtension(SRegMessage.OPENID_NS_SREG);
					if (extension instanceof SRegResponse) {
						SRegResponse sRegResponse = (SRegResponse) extension;
						details.setMail(sRegResponse.getAttributeValue("email"));
						details.setName(sRegResponse.getAttributeValue("fullname"));
					}
				}
				getWebSession().onOpenIdAuthSuccess(details);
			} else {
				getWebSession().onOpenIdAuthError();
			}

		} catch (MessageException e) {
			throw new WicketRuntimeException("verification failed", e);
		} catch (DiscoveryException e) {
			throw new WicketRuntimeException("discovery failed", e);
		} catch (AssociationException e) {
			throw new WicketRuntimeException("association failed", e);
		}
	}

	private ConsumerManager getConsumerManager() {
		Application app = Session.get().getApplication();
		ConsumerManager consumerManager = app.getMetaData(CONSUMER_MANAGER_KEY);
		if (consumerManager == null) {
			// double checked locking
			synchronized (CONSUMER_MANAGER_KEY) {
				consumerManager = app.getMetaData(CONSUMER_MANAGER_KEY);
				if (consumerManager == null) {
					consumerManager = new ConsumerManager();
					consumerManager.setAssociations(new InMemoryConsumerAssociationStore());
					consumerManager.setNonceVerifier(new InMemoryNonceVerifier(10000));
					app.setMetaData(CONSUMER_MANAGER_KEY, consumerManager);
				}
			}
		}
		return consumerManager;
	}

	public void redirect(String openId) {
		discoveryInformation = performDiscovery(openId);

		AuthRequest authRequest = createOpenIdAuthRequest();

		RequestCycle.get().setRedirect(false);
		WicketUtils.getResponse().redirect(authRequest.getDestinationUrl(true));
	}

	private AuthRequest createOpenIdAuthRequest() {
		try {
			ConsumerManager consumerManager = OpenIdSession.get().getConsumerManager();

			AuthRequest auth = consumerManager.authenticate(discoveryInformation, OpenIdSession.get()
					.getOpenIdReturnUrl());

			if (discoveryInformation.getTypes().contains(AxMessage.OPENID_NS_AX)) {
				FetchRequest fetch = FetchRequest.createFetchRequest();
				fetch.addAttribute("email", "http://axschema.org/contact/email", true);
				fetch.addAttribute("fullname", "http://axschema.org/namePerson", false);
				fetch.addAttribute("firstname", "http://axschema.org/namePerson/first", false);
				fetch.addAttribute("lastname", "http://axschema.org/namePerson/last", false);
				auth.addExtension(fetch);
			} else if (discoveryInformation.getTypes().contains(SRegMessage.OPENID_NS_SREG)) {
				SRegRequest sregReq = SRegRequest.createFetchRequest();

				sregReq.addAttribute("fullname", true);
				sregReq.addAttribute("email", true);

				auth.addExtension(sregReq);
			}

			return auth;
		} catch (MessageException e) {
			throw new WicketRuntimeException("failed to create OpenID AuthRequest", e);
		} catch (ConsumerException e) {
			throw new WicketRuntimeException("failed to create OpenID AuthRequest", e);
		}
	}

	private DiscoveryInformation performDiscovery(String openId) {
		try {
			ConsumerManager consumerManager = getConsumerManager();
			List<?> discoveries = consumerManager.discover(openId);
			return consumerManager.associate(discoveries);
		} catch (DiscoveryException e) {
			throw new WicketRuntimeException("discovery failed", e);
		}
	}

}
