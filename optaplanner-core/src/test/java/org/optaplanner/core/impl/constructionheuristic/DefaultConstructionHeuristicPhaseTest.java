/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.constructionheuristic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.immovable.TestdataImmovableEntity;
import org.optaplanner.core.impl.testdata.domain.immovable.TestdataImmovableSolution;
import org.optaplanner.core.impl.testdata.domain.reinitialize.TestdataReinitializeEntity;
import org.optaplanner.core.impl.testdata.domain.reinitialize.TestdataReinitializeSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCode;

public class DefaultConstructionHeuristicPhaseTest {

    @Test
    public void solveWithInitializedEntities() {
        SolverFactory<TestdataSolution> solverFactory = PlannerTestUtils.buildSolverFactory(
                TestdataSolution.class, TestdataEntity.class);
        solverFactory.getSolverConfig().setPhaseConfigList(Collections.singletonList(
                new ConstructionHeuristicPhaseConfig()));
        Solver<TestdataSolution> solver = solverFactory.buildSolver();

        TestdataSolution solution = new TestdataSolution("s1");
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        solution.setValueList(Arrays.asList(v1, v2, v3));
        solution.setEntityList(Arrays.asList(
                new TestdataEntity("e1", null),
                new TestdataEntity("e2", v2),
                new TestdataEntity("e3", v1)));

        solution = solver.solve(solution);
        assertNotNull(solution);
        TestdataEntity solvedE1 = solution.getEntityList().get(0);
        assertCode("e1", solvedE1);
        assertNotNull(solvedE1.getValue());
        TestdataEntity solvedE2 = solution.getEntityList().get(1);
        assertCode("e2", solvedE2);
        assertEquals(v2, solvedE2.getValue());
        TestdataEntity solvedE3 = solution.getEntityList().get(2);
        assertCode("e3", solvedE3);
        assertEquals(v1, solvedE3.getValue());
        assertEquals(0, solution.getScore().getInitScore());
    }

    @Test
    public void solveWithImmovableEntities() {
        SolverFactory<TestdataImmovableSolution> solverFactory = PlannerTestUtils.buildSolverFactory(
                TestdataImmovableSolution.class, TestdataImmovableEntity.class);
        solverFactory.getSolverConfig().setPhaseConfigList(Collections.singletonList(
                new ConstructionHeuristicPhaseConfig()));
        Solver<TestdataImmovableSolution> solver = solverFactory.buildSolver();

        TestdataImmovableSolution solution = new TestdataImmovableSolution("s1");
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        solution.setValueList(Arrays.asList(v1, v2, v3));
        solution.setEntityList(Arrays.asList(
                new TestdataImmovableEntity("e1", null, false),
                new TestdataImmovableEntity("e2", v2, true),
                new TestdataImmovableEntity("e3", null, true)));

        solution = solver.solve(solution);
        assertNotNull(solution);
        TestdataImmovableEntity solvedE1 = solution.getEntityList().get(0);
        assertCode("e1", solvedE1);
        assertNotNull(solvedE1.getValue());
        TestdataImmovableEntity solvedE2 = solution.getEntityList().get(1);
        assertCode("e2", solvedE2);
        assertEquals(v2, solvedE2.getValue());
        TestdataImmovableEntity solvedE3 = solution.getEntityList().get(2);
        assertCode("e3", solvedE3);
        assertEquals(null, solvedE3.getValue());
        assertEquals(-1, solution.getScore().getInitScore());
    }

    @Test
    public void solveWithReinitializeVariable() {
        SolverFactory<TestdataReinitializeSolution> solverFactory = PlannerTestUtils.buildSolverFactory(
                TestdataReinitializeSolution.class, TestdataReinitializeEntity.class);
        solverFactory.getSolverConfig().setPhaseConfigList(Collections.singletonList(
                new ConstructionHeuristicPhaseConfig()));
        Solver<TestdataReinitializeSolution> solver = solverFactory.buildSolver();

        TestdataReinitializeSolution solution = new TestdataReinitializeSolution("s1");
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        solution.setValueList(Arrays.asList(v1, v2, v3));
        solution.setEntityList(Arrays.asList(
                new TestdataReinitializeEntity("e1", null, false),
                new TestdataReinitializeEntity("e2", v2, false),
                new TestdataReinitializeEntity("e3", v2, true),
                new TestdataReinitializeEntity("e4", null, true)));

        solution = solver.solve(solution);
        assertNotNull(solution);
        TestdataReinitializeEntity solvedE1 = solution.getEntityList().get(0);
        assertCode("e1", solvedE1);
        assertNotNull(solvedE1.getValue());
        TestdataReinitializeEntity solvedE2 = solution.getEntityList().get(1);
        assertCode("e2", solvedE2);
        assertNotNull(solvedE2.getValue());
        TestdataReinitializeEntity solvedE3 = solution.getEntityList().get(2);
        assertCode("e3", solvedE3);
        assertEquals(v2, solvedE3.getValue());
        TestdataReinitializeEntity solvedE4 = solution.getEntityList().get(3);
        assertCode("e4", solvedE4);
        assertEquals(null, solvedE4.getValue());
        assertEquals(-1, solution.getScore().getInitScore());
    }

}
