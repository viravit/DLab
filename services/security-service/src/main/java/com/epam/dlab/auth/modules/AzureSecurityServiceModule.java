package com.epam.dlab.auth.modules;

import com.epam.dlab.auth.SecurityServiceConfiguration;
import com.epam.dlab.auth.UserInfoDAO;
import com.epam.dlab.auth.UserVerificationService;
import com.epam.dlab.auth.azure.AzureAuthenticationResource;
import com.epam.dlab.auth.azure.AzureLoginUrlBuilder;
import com.epam.dlab.auth.azure.AzureSecurityResource;
import com.epam.dlab.auth.azure.service.AzureAuthorizationCodeService;
import com.epam.dlab.auth.azure.service.AzureAuthorizationCodeServiceImpl;
import com.epam.dlab.auth.conf.AzureLoginConfiguration;
import com.epam.dlab.auth.resources.SynchronousLdapAuthenticationService;
import com.epam.dlab.cloud.CloudModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.dropwizard.setup.Environment;

import java.io.IOException;

public class AzureSecurityServiceModule extends CloudModule {
	private final SecurityServiceConfiguration conf;

	AzureSecurityServiceModule(SecurityServiceConfiguration configuration) {
		this.conf = configuration;
	}

	@Override
	protected void configure() {
		bind(UserVerificationService.class).toInstance(SecurityServiceModule.defaultUserVerificationService());
		final AzureLoginConfiguration azureLoginConfiguration = conf.getAzureLoginConfiguration();
		bind(AzureLoginConfiguration.class).toInstance(azureLoginConfiguration);
		if (!azureLoginConfiguration.isUseLdap()) {
			bind(AzureLoginUrlBuilder.class).toInstance(new AzureLoginUrlBuilder(azureLoginConfiguration));
			try {
				bind(AzureAuthorizationCodeService.class).toInstance(
						new AzureAuthorizationCodeServiceImpl(azureLoginConfiguration.getAuthority() +
								azureLoginConfiguration.getTenant() + "/", azureLoginConfiguration
								.getPermissionScope(),
								azureLoginConfiguration.getManagementApiAuthFile(), azureLoginConfiguration
								.isValidatePermissionScope()));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void init(Environment environment, Injector injector) {

		if (conf.getAzureLoginConfiguration().isUseLdap()) {
			environment.jersey().register(injector.getInstance(SynchronousLdapAuthenticationService.class));
		} else {
			environment.jersey().register(injector.getInstance(AzureAuthenticationResource.class));
			environment.jersey().register(injector.getInstance(AzureSecurityResource.class));
		}
	}

	@Provides
	@Singleton
	private AzureAuthenticationResource azureAuthenticationService(UserInfoDAO userInfoDao,
																   AzureAuthorizationCodeService
																		   authorizationCodeService) {
		return new AzureAuthenticationResource(conf, userInfoDao, conf.getAzureLoginConfiguration(),
				authorizationCodeService);
	}
}
