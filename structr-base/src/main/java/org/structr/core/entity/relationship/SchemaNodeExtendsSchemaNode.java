/*
 * Copyright (C) 2010-2023 Structr GmbH
 *
 * This file is part of Structr <http://structr.org>.
 *
 * Structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Structr.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.structr.core.entity.relationship;

import org.structr.core.entity.ManyToOne;
import org.structr.core.entity.SchemaNode;

/**
 *
 *
 */
public class SchemaNodeExtendsSchemaNode extends ManyToOne<SchemaNode, SchemaNode>  {

	@Override
	public Class<SchemaNode> getSourceType() {
		return SchemaNode.class;
	}

	@Override
	public Class<SchemaNode> getTargetType() {
		return SchemaNode.class;
	}

	@Override
	public String name() {
		return "EXTENDS";
	}

	@Override
	public int getCascadingDeleteFlag() {
		return 0;
	}

	@Override
	public boolean isInternal() {
		return true;
	}

	@Override
	public int getAutocreationFlag() {
		return 0;
	}
}
