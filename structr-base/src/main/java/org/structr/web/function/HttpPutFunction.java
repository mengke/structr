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
package org.structr.web.function;

import org.structr.common.error.FrameworkException;
import org.structr.core.GraphObjectMap;
import org.structr.core.property.IntProperty;
import org.structr.core.property.StringProperty;
import org.structr.rest.common.HttpHelper;
import org.structr.schema.action.ActionContext;

import java.util.Map;

public class HttpPutFunction extends UiAdvancedFunction {

	public static final String ERROR_MESSAGE_PUT    = "Usage: ${PUT(URL, body [, contentType, charset])}. Example: ${PUT('http://localhost:8082/structr/rest/folders/6aa10d68569d45beb384b42a1fc78c50', '{name:\"Test\"}', 'application/json', 'utf-8')}";
	public static final String ERROR_MESSAGE_PUT_JS = "Usage: ${{Structr.PUT(URL, body [, contentType, charset])}}. Example: ${{Structr.PUT('http://localhost:8082/structr/rest/folders/6aa10d68569d45beb384b42a1fc78c50', '{name:\"Test\"}', 'application/json', 'utf-8')}}";

	@Override
	public String getName() {
		return "PUT";
	}

	@Override
	public String getSignature() {
		return "url, body [, contentType, charset ]";
	}

	@Override
	public Object apply(final ActionContext ctx, final Object caller, final Object[] sources) throws FrameworkException {

		try {

			assertArrayHasMinLengthAndAllElementsNotNull(sources, 2);

			final String uri = sources[0].toString();
			final String body = sources[1].toString();
			String contentType = "application/json";
			String charset = "utf-8";

			// override default content type
			if (sources.length >= 3 && sources[2] != null) {
				contentType = sources[2].toString();
			}

			// override default content type
			if (sources.length >= 4 && sources[3] != null) {
				charset = sources[3].toString();
			}

			final Map<String, String> responseData = HttpHelper.put(uri, body, null, null, ctx.getHeaders(), charset, ctx.isValidateCertificates());

			final int statusCode = Integer.parseInt(responseData.get("status"));
			responseData.remove("status");

			final String responseBody = responseData.get("body");
			responseData.remove("body");

			final GraphObjectMap response = new GraphObjectMap();

			if ("application/json".equals(contentType)) {

				final FromJsonFunction fromJsonFunction = new FromJsonFunction();
				response.setProperty(new StringProperty("body"), fromJsonFunction.apply(ctx, caller, new Object[]{responseBody}));

			} else {

				response.setProperty(new StringProperty("body"), responseBody);
			}

			response.setProperty(new IntProperty("status"), statusCode);

			final GraphObjectMap map = new GraphObjectMap();

			for (final Map.Entry<String, String> entry : responseData.entrySet()) {

				map.put(new StringProperty(entry.getKey()), entry.getValue());
			}

			response.setProperty(new StringProperty("headers"), map);

			return response;

		} catch (IllegalArgumentException e) {

			logParameterError(caller, sources, e.getMessage(), ctx.isJavaScriptContext());
			return usage(ctx.isJavaScriptContext());
		}
	}

	@Override
	public String usage(boolean inJavaScriptContext) {
		return (inJavaScriptContext ? ERROR_MESSAGE_PUT_JS : ERROR_MESSAGE_PUT);
	}

	@Override
	public String shortDescription() {
		return "Sends an HTTP PUT request to the given URL and returns the response body";
	}
}
