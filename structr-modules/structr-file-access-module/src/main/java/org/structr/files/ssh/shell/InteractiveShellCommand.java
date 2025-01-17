/*
 * Copyright (C) 2010-2023 Structr GmbH
 *
 * This file is part of Structr <http://structr.org>.
 *
 * Structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Structr.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.structr.files.ssh.shell;

import org.structr.files.ssh.StructrShellCommand;

import java.io.IOException;
import java.util.List;

/**
 *
 *
 */
public abstract class InteractiveShellCommand extends AbstractShellCommand {

	@Override
	public void handleExit() {
	}

	@Override
	public void execute(final StructrShellCommand parent) throws IOException {
		term.setTerminalHandler(this);
	}

	@Override
	public List<String> getCommandHistory() {
		return null;
	}

	@Override
	public void handleLogoutRequest() throws IOException {
	}

	@Override
	public void handleCtrlC() throws IOException {

		term.print("^C");
		term.clearLineBuffer();
		term.restoreRootTerminalHandler();
		term.handleNewline();
	}
}
