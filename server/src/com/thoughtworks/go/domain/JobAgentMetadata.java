/*
 * Copyright 2016 ThoughtWorks, Inc.
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

package com.thoughtworks.go.domain;

import com.google.gson.Gson;
import com.thoughtworks.go.config.JobAgentConfig;
import com.thoughtworks.go.domain.config.ConfigurationKey;
import com.thoughtworks.go.domain.config.ConfigurationProperty;
import com.thoughtworks.go.domain.config.ConfigurationValue;
import com.thoughtworks.go.util.ListUtil;
import com.thoughtworks.go.util.MapUtil;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class JobAgentMetadata extends PersistentObject {
    private Long jobId;
    private String metadata;
    private String metadataVersion;

    private JobAgentMetadata() {

    }

    public JobAgentMetadata(long jobId, JobAgentConfig config) {
        this.jobId = jobId;
        this.metadata = toJSON(config);
        this.metadataVersion = "1.0";
    }

    public JobAgentConfig jobAgentConfig() {
        Gson gson = new Gson();
        Map map = gson.fromJson(metadata, LinkedHashMap.class);
        String pluginId = (String) map.get("pluginId");
        Map<String, String> properties = (Map<String, String>) map.get("properties");

        Collection<ConfigurationProperty> configProperties = MapUtil.collect(properties, new ListUtil.Transformer<Map.Entry<String, String>, ConfigurationProperty>() {
            @Override
            public ConfigurationProperty transform(Map.Entry<String, String> entry) {
                return new ConfigurationProperty(new ConfigurationKey(entry.getKey()), new ConfigurationValue(entry.getValue()));
            }
        });

        return new JobAgentConfig(pluginId, configProperties);
    }

    private static String toJSON(JobAgentConfig agentConfig) {
        Gson gson = new Gson();
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("pluginId", agentConfig.getPluginId());
        map.put("properties", agentConfig.getConfigurationAsMap(true));
        return gson.toJson(map);
    }

}
