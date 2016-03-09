package com.github.cukedoctor.renderer;

import com.github.cukedoctor.api.model.Feature;
import com.github.cukedoctor.api.model.Scenario;
import com.github.cukedoctor.api.model.Step;
import com.github.cukedoctor.spi.ExamplesRenderer;
import com.github.cukedoctor.spi.ScenarioRenderer;
import com.github.cukedoctor.spi.StepsRenderer;
import com.github.cukedoctor.spi.TagsRenderer;

import java.util.List;
import java.util.ServiceLoader;

import static com.github.cukedoctor.util.Assert.hasText;

/**
 * Created by pestano on 27/02/16.
 */
public class CukedoctorScenarioRenderer extends AbstractBaseRenderer implements ScenarioRenderer {


    TagsRenderer tagsRenderer;

    ExamplesRenderer examplesRenderer;

    StepsRenderer stepsRenderer;
    
    
    public CukedoctorScenarioRenderer() {
        ServiceLoader<TagsRenderer> tagsRenderers = ServiceLoader.load(TagsRenderer.class);
        ServiceLoader<ExamplesRenderer> examplesRenderers = ServiceLoader.load(ExamplesRenderer.class);
        ServiceLoader<StepsRenderer> stepsRenderers = ServiceLoader.load(StepsRenderer.class);

        if (tagsRenderers.iterator().hasNext()) {
            tagsRenderer = tagsRenderers.iterator().next();
        } else {
            tagsRenderer = new CukedoctorTagsRenderer();
            tagsRenderer.setI18n(i18n);
        }

        if (examplesRenderers.iterator().hasNext()) {
            examplesRenderer = examplesRenderers.iterator().next();
        } else {
            examplesRenderer = new CukedoctorExamplesRenderer();
            examplesRenderer.setI18n(i18n);
        }

        if (stepsRenderers.iterator().hasNext()) {
            stepsRenderer = stepsRenderers.iterator().next();
        } else {
            stepsRenderer = new CukedoctorStepsRenderer();
            stepsRenderer.setI18n(i18n);
        }
    }

    @Override
    public String renderScenario(Scenario scenario, Feature feature) {
        docBuilder.clear();
        if (scenario.hasIgnoreDocsTag()) {
            return "";
        }
        
        if(scenario.isBackground() && feature.isBackgroundRendered()){
            return "";
        }
        
        if(!feature.isBackgroundRendered() && scenario.isBackground()){
            feature.setBackgroundRendered(true);
            docBuilder.sectionTitleLevel3(scenario.getKeyword());
        } 

        if (hasText(scenario.getName())) {
            docBuilder.sectionTitleLevel3(new StringBuilder(scenario.getKeyword()).
                    append(": ").append(scenario.getName()).toString());
        }
         
        if (feature.hasTags() || scenario.hasTags()) {
            docBuilder.append(renderScenarioTags(scenario, feature));
        }

        docBuilder.textLine(scenario.getDescription()).newLine();

        if (scenario.hasExamples()) {
            docBuilder.append(renderScenarioExamples(scenario));
            return docBuilder.toString();//or a scenario has examples or it has steps
        }

        if (scenario.hasSteps()) {
            docBuilder.append(renderScenarioSteps(scenario.getSteps()));
        }
        return docBuilder.toString();
    }

    String renderScenarioSteps(List<Step> scenarioSteps) {
        return stepsRenderer.renderSteps(scenarioSteps);
    }

    String renderScenarioExamples(Scenario scenario) {
        return examplesRenderer.renderScenarioExamples(scenario);
    }

    String renderScenarioTags(Scenario scenario, Feature feature) {
        return tagsRenderer.renderScenarioTags(feature, scenario);
    }


}