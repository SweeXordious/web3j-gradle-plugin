package org.web3j.gradle.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.impldep.aQute.bnd.build.Run;
import org.gradle.workers.WorkerExecutor;
import org.web3j.codegen.SolidityFunctionWrapperGenerator;
import org.web3j.openapi.codegen.utils.GeneratorUtils;
import org.web3j.openapi.codegen.GenerateOpenApi;
import org.web3j.openapi.codegen.config.GeneratorConfiguration;

import javax.inject.Inject;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class OpenApiGenerator extends DefaultTask implements Runnable {
    private WorkerExecutor executor = null;

    @Input
    private String projectName;

    @Input private List<File> contractsBin;
    @Input private List<File> contractsAbi;

    @Input private String outputDir;
    @Input private String packageName;

    @Input @Optional
    private int addressLength;

    @Input @Optional private String contextPath;

    @Inject
    public OpenApiGenerator(final WorkerExecutor executor) {
        this.executor = executor;
    }

    public OpenApiGenerator(String projectName,
                            List<File> contractsBin,
                            List<File> contractsAbi,
                            String outputDir,
                            String packageName,
                            int addressLength,
                            String contextPath) {
        this.projectName = projectName;
        this.contractsBin = contractsBin;
        this.contractsAbi = contractsAbi;
        this.outputDir = outputDir;
        this.packageName = packageName;
        this.addressLength = addressLength;
        this.contextPath = contextPath;
    }

    @TaskAction
    void generateOpenApi() {
        if (contractsBin.isEmpty()) contractsBin = contractsAbi;

        executor.submit(
                OpenApiGenerator.class,
                configuration -> {
                    configuration.setParams(
                            projectName,
                            contractsBin,
                            contractsAbi,
                            outputDir,
                            packageName,
                            addressLength,
                            contextPath
                    );
                }
        );
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

    @Override
    public void run() {
        GeneratorConfiguration generatorConfiguration = new GeneratorConfiguration(
                projectName,
                packageName,
                outputDir,
                GeneratorUtils.loadContractConfigurations(
                        contractsAbi,
                        contractsBin
                ),
                addressLength,
                contextPath,
                // The following parameters should be moved to the init block in OpenAPI codegen
                "0.0.1",
                projectName
        );
        // This is not generating the SwaggerUI
        GenerateOpenApi generateOpenApi = new GenerateOpenApi(generatorConfiguration);
        generateOpenApi.generateAll();
    }
}
