/*
 * Copyright 2020 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.web3j.gradle.plugin;

import java.io.File;
import java.util.List;
import javax.inject.Inject;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

import org.web3j.openapi.codegen.GenerateOpenApi;
import org.web3j.openapi.codegen.config.GeneratorConfiguration;
import org.web3j.openapi.codegen.utils.GeneratorUtils;

public class OpenApiGenerator extends DefaultTask {

    @Input private String projectName;

    @Input private List<File> contractsBin;
    @Input private List<File> contractsAbi;

    @Input private String outputDir;
    @Input private String packageName;

    @Input @Optional private int addressLength;

    @Input @Optional private String contextPath;

    @Inject
    public OpenApiGenerator() {}

    @TaskAction
    void generateOpenApi() {
        GeneratorConfiguration generatorConfiguration =
                new GeneratorConfiguration(
                        projectName,
                        packageName,
                        outputDir,
                        GeneratorUtils.loadContractConfigurations(contractsAbi, contractsBin),
                        addressLength,
                        contextPath,
                        // The following parameters should be moved to the init block in OpenAPI
                        // codegen
                        "0.0.1",
                        projectName);
        // This is not generating the SwaggerUI
        GenerateOpenApi generateOpenApi = new GenerateOpenApi(generatorConfiguration);
        generateOpenApi.generateAll();
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<File> getContractsBin() {
        return contractsBin;
    }

    public void setContractsBin(List<File> contractsBin) {
        this.contractsBin = contractsBin;
    }

    public List<File> getContractsAbi() {
        return contractsAbi;
    }

    public void setContractsAbi(List<File> contractsAbi) {
        this.contractsAbi = contractsAbi;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getAddressLength() {
        return addressLength;
    }

    public void setAddressLength(int addressLength) {
        this.addressLength = addressLength;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }
}
