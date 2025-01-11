package edu.uw.cs.lil.uwtime.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import edu.uw.cs.lil.uwtime.utils.TemporalDomain;

public class TemporalHandler extends AbstractHandler {
	private final TemporalAnnotator	annotator;

	public TemporalHandler() throws ClassNotFoundException, IOException {
		annotator = new TemporalAnnotator();
	}

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		response.setContentType("application/json;charset=utf-8");
		final String query = request.getParameter("query");
		final String domain = request.getParameter("domain");
		final String dct = request.getParameter("dct");
		if (query != null && domain != null && dct != null) {
			System.out.println("===============");
			System.out.println("Received:");
			System.out.println("Query = " + query);
			System.out.println("Domain = " + domain);
			System.out.println("DCT = " + dct);
			System.out.println("---------------");
			try {
				final DocumentAnnotation da = annotator.extract(query, dct,
						TemporalDomain.valueOf(domain.toUpperCase()));
				System.out.println(da);
				System.out.println("===============");
				response.getWriter().println(da.toJSON());
				response.setStatus(HttpServletResponse.SC_OK);
			} catch (final IllegalArgumentException e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		baseRequest.setHandled(true);
	}
}