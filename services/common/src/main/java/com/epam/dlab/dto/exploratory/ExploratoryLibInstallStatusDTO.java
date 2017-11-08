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


package com.epam.dlab.dto.exploratory;

import com.epam.dlab.dto.StatusEnvBaseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects.ToStringHelper;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExploratoryLibInstallStatusDTO extends StatusEnvBaseDTO<ExploratoryLibInstallStatusDTO> {
    @JsonProperty
    private List<LibInstallDTO> libs;

    @JsonProperty
    private String computationalName;


    public ExploratoryLibInstallStatusDTO withLibs(List<LibInstallDTO> libs) {
        setLibs(libs);
        return this;
    }

    public ExploratoryLibInstallStatusDTO withComputationalName(String computationalName) {
        setComputationalName(computationalName);
        return this;
    }

    @Override
    public ToStringHelper toStringHelper(Object self) {
        return super.toStringHelper(self)
                .add("libs", libs)
                .add("computationalName", computationalName);
    }
}
