package org.evomaster.core.problem.rest.service.sampler

import com.webfuzzing.arazzo.models.domain.Step
import com.webfuzzing.arazzo.models.domain.Workflow
import org.evomaster.core.problem.enterprise.SampleType
import org.evomaster.core.problem.rest.data.RestCallAction
import org.evomaster.core.problem.rest.data.RestIndividual

class ArazzoSampler : AbstractRestSampler() {

    private lateinit var workflowWeights: Map<String, Double>

    override fun customizeAdHocInitialIndividuals() {
        // workflows are loaded in AbstractRestSampler.initialize() when arazzoStrategy is enabled
    }

    override fun hasSpecialInitForSmartSampler(): Boolean = false

    override fun postInits() {
        workflowWeights = resolveWorkflowWeights(workflowsArazzo.map { it.workflowId })
    }

    /**
     * Choose a workflow according to configured weights
     */
    override fun sampleAtRandom(): RestIndividual {
        return buildIndividualFromWorkflow(chooseWorkflow())
    }

    /**
     * Create workflows individuals
     */
    fun buildIndividualFromWorkflow(workflow: Workflow): RestIndividual {
        val actions = workflow.steps
            .flatMap { resolveStep(it) }
            .onEach {
                it.doInitialize(randomness)
                it.forceNewTaints()
            }
            .toMutableList()

        return createIndividual(SampleType.RANDOM, actions)
    }

    /**
     * Choose a workflow by probability
     */
    private fun chooseWorkflow(): Workflow {
        val id = randomness.chooseByProbability(workflowWeights)
        return workflowsArazzoById.getValue(id)
    }

    /**
     * Resolve workflow weights
     */
    private fun resolveWorkflowWeights(workflowIds: Collection<String>): Map<String, Double> {
        if (config.arazzoWorkflowWeights.isBlank()) {
            return equalWeights(workflowIds)
        }

        val configured = parseConfiguredWeights(workflowIds)
        if (configured.isEmpty()) {
            return equalWeights(workflowIds)
        }

        val sum = configured.values.sum()
        val unspecified = workflowIds.filter { it !in configured.keys }

        val weights = mutableMapOf<String, Double>()

        if (sum < 1.0) {
            configured.forEach { (id, weight) -> weights[id] = weight }
            if (unspecified.isNotEmpty()) {
                val remainder = (1.0 - sum) / unspecified.size
                unspecified.forEach { weights[it] = remainder }
            }
        } else {
            configured.forEach { (id, weight) -> weights[id] = weight / sum }
            unspecified.forEach { weights[it] = 0.0 }
        }

        return weights
    }

    /**
     * Search for weights configured
     */
    private fun parseConfiguredWeights(workflowIds: Collection<String>): Map<String, Double> {
        val validIds = workflowIds.toSet()
        val configured = mutableMapOf<String, Double>()

        for (token in config.arazzoWorkflowWeights.split(',')) {
            val trimmed = token.trim()
            if (trimmed.isEmpty()) continue

            val parts = trimmed.split(':', limit = 2)
            if (parts.size != 2) continue

            val id = parts[0].trim()
            val weight = parts[1].trim().toDoubleOrNull() ?: continue
            if (id in validIds && weight > 0.0) {
                configured[id] = weight
            }
        }

        return configured
    }

    /**
     * Distribute the same weight for all workflowIds
     */
    private fun equalWeights(workflowIds: Collection<String>): Map<String, Double> {
        val weight = 1.0 / workflowIds.size
        return workflowIds.associateWith { weight }
    }

    /**
     * The Steps can reference a sub-workflow or OpenApi
     */
    private fun resolveStep(step: Step): List<RestCallAction> {
        if (!step.operationId.isNullOrBlank()) {
            return listOfNotNull(findActionForOperation(step.operationId))
        }
        if (!step.workflowId.isNullOrBlank()) {
            val nested = workflowsArazzoById[step.workflowId] ?: return emptyList()
            return nested.steps.flatMap { resolveStep(it) }
        }
        return emptyList()
    }

    /**
     * Every Step Arazzo has its corresponding RestCallAction in the actionCluster
     */
    private fun findActionForOperation(operationId: String): RestCallAction? {
        val template = actionCluster.values
            .filterIsInstance<RestCallAction>()
            .find { it.operationId == operationId }
        return template?.copy() as? RestCallAction
    }
}
