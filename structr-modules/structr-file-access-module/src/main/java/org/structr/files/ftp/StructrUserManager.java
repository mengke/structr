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
package org.structr.files.ftp;

import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.structr.api.util.Iterables;
import org.structr.common.AccessMode;
import org.structr.common.SecurityContext;
import org.structr.common.error.FrameworkException;
import org.structr.core.app.StructrApp;
import org.structr.core.entity.AbstractNode;
import org.structr.core.entity.Principal;
import org.structr.core.graph.Tx;
import org.structr.rest.auth.AuthHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 *
 */
public class StructrUserManager implements UserManager {

	private static final Logger logger = LoggerFactory.getLogger(StructrUserManager.class.getName());

	private SecurityContext securityContext = null;

	@Override
	public User getUserByName(String userName) throws FtpException {

		try (Tx tx = StructrApp.getInstance(securityContext).tx()) {

			org.structr.web.entity.User structrUser = getStructrUser(userName);
			tx.success();
			if (structrUser != null) {
				return new StructrFtpUser(securityContext, structrUser);
			} else {
				return null;
			}
		} catch (FrameworkException fex) {
			logger.error("Unable to get user by its name", fex);
		}
		return null;
	}

	@Override
	public String[] getAllUserNames() throws FtpException {

		try (Tx tx = StructrApp.getInstance(securityContext).tx()) {

			final List<String> userNames = new ArrayList();
			final List<Principal> result = new LinkedList<>();

			try {

				Iterables.addAll(result, StructrApp.getInstance(securityContext).nodeQuery(Principal.class).sort(AbstractNode.name).getResultStream());

			} catch (FrameworkException ex) {

				logger.warn("Error while searching for principal", ex);

			}

			if (!result.isEmpty()) {

				for (Principal p : result) {

					userNames.add(p.getProperty(Principal.name));
				}
			}

			tx.success();


			return (String[]) userNames.toArray(new String[userNames.size()]);

		} catch (FrameworkException fex) {
			logger.error("Unable to get user by its name", fex);
		}

		return null;
	}

	@Override
	public void delete(String string) throws FtpException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void save(User user) throws FtpException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean doesExist(String string) throws FtpException {

		try (Tx tx = StructrApp.getInstance(securityContext).tx()) {

			boolean exists = (getStructrUser(string) != null);

			tx.success();

			return exists;

		} catch (FrameworkException fex) {
			logger.error("Unable to determine if user " + string + " exists", fex);
		}

		return false;
	}

	@Override
	public User authenticate(Authentication auth) throws AuthenticationFailedException {

		logger.debug("Authentication: {}", auth);
		String userName = null;
		String password = null;

		if (auth instanceof UsernamePasswordAuthentication) {

			org.structr.web.entity.User user = null;

			// Use superuser context for authentication only
			try (Tx tx = StructrApp.getInstance().tx()) {

				UsernamePasswordAuthentication authentication = (UsernamePasswordAuthentication) auth;

				userName = authentication.getUsername();
				password = authentication.getPassword();

				user = (org.structr.web.entity.User) AuthHelper.getPrincipalForPassword(Principal.name, userName, password);

				securityContext = SecurityContext.getInstance(user, AccessMode.Backend);

				tx.success();

			} catch (FrameworkException ex) {
				logger.warn("FTP authentication attempt failed with username {} and password {}", new Object[]{userName, password});
			}

			if (user != null) {

				return new StructrFtpUser(securityContext, user);

			}

		}

		throw new AuthenticationFailedException("No structr user found for credentials " + userName + "/" + password);
	}

	@Override
	public String getAdminName() throws FtpException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean isAdmin(String string) throws FtpException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private org.structr.web.entity.User getStructrUser(final String userName) {

		try (Tx tx = StructrApp.getInstance(securityContext).tx()) {

			final org.structr.web.entity.User user = (org.structr.web.entity.User) AuthHelper.getPrincipalForCredential(Principal.name, userName);

			tx.success();

			return user;

		} catch (FrameworkException fex) {
			logger.error("Unable to get user by its name", fex);
		}

		return null;
	}
}
