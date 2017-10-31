/*
 * Copyright (c) 2017, EPAM SYSTEMS INC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.dlab.backendapi.modules;

import com.epam.dlab.backendapi.dao.KeyDAO;
import com.epam.dlab.backendapi.dao.azure.AzureKeyDao;
import com.epam.dlab.backendapi.domain.azure.BillingSchedulerManagerAzure;
import com.epam.dlab.backendapi.resources.azure.ComputationalResourceAzure;
import com.epam.dlab.backendapi.resources.callback.azure.EdgeCallbackAzure;
import com.epam.dlab.backendapi.resources.callback.azure.KeyUploaderCallbackAzure;
import com.epam.dlab.backendapi.service.AzureBillingService;
import com.epam.dlab.backendapi.service.AzureInfrastructureInfoService;
import com.epam.dlab.backendapi.service.BillingService;
import com.epam.dlab.backendapi.service.InfrastructureInfoService;
import com.epam.dlab.cloud.CloudModule;
import com.google.inject.Injector;
import io.dropwizard.setup.Environment;

public class AzureSelfServiceModule extends CloudModule {

    @Override
    protected void configure() {
        bind(BillingService.class).to(AzureBillingService.class);
        bind((KeyDAO.class)).to(AzureKeyDao.class);
        bind(InfrastructureInfoService.class).to(AzureInfrastructureInfoService.class);
    }

    @Override
    public void init(Environment environment, Injector injector) {
        environment.jersey().register(injector.getInstance(EdgeCallbackAzure.class));
        environment.jersey().register(injector.getInstance(KeyUploaderCallbackAzure.class));
        environment.jersey().register(injector.getInstance(ComputationalResourceAzure.class));
        environment.lifecycle().manage(injector.getInstance(BillingSchedulerManagerAzure.class));
    }
}
