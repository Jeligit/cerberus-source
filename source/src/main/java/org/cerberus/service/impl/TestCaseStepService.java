/*
 * Cerberus  Copyright (C) 2013  vertigo17
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This file is part of Cerberus.
 *
 * Cerberus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cerberus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cerberus.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.cerberus.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Level;
import org.cerberus.dao.ITestCaseStepDAO;
import org.cerberus.entity.TestCaseStep;
import org.cerberus.exception.CerberusException;
import org.cerberus.log.MyLogger;
import org.cerberus.service.ITestCaseStepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author bcivel
 */
@Service
public class TestCaseStepService implements ITestCaseStepService {

    @Autowired
    private ITestCaseStepDAO testCaseStepDAO;

    @Override
    public List<TestCaseStep> getListOfSteps(String test, String testcase) {
        return testCaseStepDAO.findTestCaseStepByTestCase(test, testcase);
    }

    @Override
    public List<String> getLoginStepFromTestCase(String countryCode, String application) {
        return testCaseStepDAO.getLoginStepFromTestCase(countryCode, application);
    }

    @Override
    public void insertTestCaseStep(TestCaseStep testCaseStep) throws CerberusException {
        testCaseStepDAO.insertTestCaseStep(testCaseStep);
    }

    @Override
    public boolean insertListTestCaseStep(List<TestCaseStep> testCaseStepList) {
        for (TestCaseStep tcs : testCaseStepList) {
            try {
                insertTestCaseStep(tcs);
            } catch (CerberusException ex) {
                MyLogger.log(TestCaseStepService.class.getName(), Level.FATAL, ex.toString());
                return false;
            }
        }
        return true;
    }

    @Override
    public TestCaseStep findTestCaseStep(String test, String testcase, Integer step) {
        return testCaseStepDAO.findTestCaseStep(test, testcase, step);
    }

    @Override
    public void updateTestCaseStep(TestCaseStep tcs) throws CerberusException {
        testCaseStepDAO.updateTestCaseStep(tcs);
    }

    @Override
    public void deleteListTestCaseStep(List<TestCaseStep> tcsToDelete) throws CerberusException {
        for (TestCaseStep tcs : tcsToDelete) {
            deleteTestCaseStep(tcs);
        }
    }

    @Override
    public void deleteTestCaseStep(TestCaseStep tcs) throws CerberusException {
        testCaseStepDAO.deleteTestCaseStep(tcs);
    }

    @Override
    public List<TestCaseStep> getTestCaseStepUsingStepInParamter(String test, String testCase, int step) throws CerberusException {
        return testCaseStepDAO.getTestCaseStepUsingStepInParamter(test, testCase, step);
    }

    @Override
    public void compareListAndUpdateInsertDeleteElements(List<TestCaseStep> newList, List<TestCaseStep> oldList) throws CerberusException {
        /**
         * Iterate on (TestCaseStep From Page - TestCaseStep From Database) If
         * TestCaseStep in Database has same key : Update and remove from the
         * list. If TestCaseStep in database does ot exist : Insert it.
         */
        List<TestCaseStep> tcsToUpdateOrInsert = new ArrayList(newList);
        tcsToUpdateOrInsert.removeAll(oldList);
        List<TestCaseStep> tcsToUpdateOrInsertToIterate = new ArrayList(tcsToUpdateOrInsert);

        for (TestCaseStep tcsDifference : tcsToUpdateOrInsertToIterate) {
            for (TestCaseStep tcsInDatabase : oldList) {
                if (tcsDifference.hasSameKey(tcsInDatabase)) {
                    this.updateTestCaseStep(tcsDifference);
                    tcsToUpdateOrInsert.remove(tcsDifference);
                }
            }
        }
        this.insertListTestCaseStep(tcsToUpdateOrInsert);

        /**
         * Iterate on (TestCaseStep From Database - TestCaseStep From Page). If
         * TestCaseStep in Page has same key : remove from the list. Then delete
         * the list of TestCaseStep
         */
        List<TestCaseStep> tcsToDelete = new ArrayList(oldList);
        tcsToDelete.removeAll(newList);
        List<TestCaseStep> tcsToDeleteToIterate = new ArrayList(tcsToDelete);

        for (TestCaseStep tcsDifference : tcsToDeleteToIterate) {
            for (TestCaseStep tcsInPage : newList) {
                if (tcsDifference.hasSameKey(tcsInPage)) {
                    tcsToDelete.remove(tcsDifference);
                }
            }
        }
        this.deleteListTestCaseStep(tcsToDelete);
    }

}
