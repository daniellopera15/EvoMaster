package org.evomaster.core.problem.rest.service.mutator

import com.google.inject.Inject
import org.evomaster.core.problem.api.service.ApiWsStructureMutator
import org.evomaster.core.problem.rest.service.sampler.ArazzoSampler
import org.evomaster.core.search.EvaluatedIndividual
import org.evomaster.core.search.Individual
import org.evomaster.core.search.service.mutator.MutatedGeneSpecification
import org.evomaster.core.sql.SqlInsertBuilder

class ArazzoStructureMutator : ApiWsStructureMutator() {

    @Inject
    private lateinit var sampler: ArazzoSampler

    override fun mutateStructure(
        individual: Individual,
        evaluatedIndividual: EvaluatedIndividual<*>,
        mutatedGenes: MutatedGeneSpecification?,
        targets: Set<Int>
    ) {
        // TODO: Not yet implemented
        return
    }

    override fun addInitializingActions(
        individual: EvaluatedIndividual<*>,
        mutatedGenes: MutatedGeneSpecification?
    ) {
        addInitializingActions(individual, mutatedGenes, sampler)
    }

    override fun getSqlInsertBuilder(): SqlInsertBuilder? {
        return sampler.sqlInsertBuilder
    }
}
