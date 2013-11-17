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
package org.cerberus.factory;

import org.cerberus.entity.Application;

/**
 * @author vertigo
 */
public interface IFactoryApplication {

    /**
     * @param application  ID of the application.
     * @param description  Description of the Application.
     * @param internal
     * @param sort
     * @param type
     * @param system
     * @param subsystem
     * @param svnurl
     * @param deploytype
     * @param mavengroupid
     * @return
     */
    Application create(String application, String description, String internal
            , int sort, String type, String system, String subsystem
            , String svnurl, String deploytype, String mavengroupid
            , String bugtrackerurl, String bugtrackernewurl);


}