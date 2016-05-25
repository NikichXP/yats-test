package eduportal.servlet;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.google.appengine.api.blobstore.*;
import eduportal.dao.entity.*;
import eduportal.model.AuthContainer;
import static com.googlecode.objectify.ObjectifyService.ofy;

public class FileProcessorServlet extends HttpServlet {
	private static final long serialVersionUID = 4212663183594760678L;

	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		System.out.println("POST");
		System.out.println(req.getParameter("foo"));
		UserEntity user = null;
		String token = req.getParameter("token");
		System.out.println(req.getParameter("token"));
		if (token == null) {
			for (Cookie c : req.getCookies()) {
				System.out.println(c.getName() + "   " + c.getValue());
				if (c.getName().equals("sesToken")) {
					user = AuthContainer.getUser(c.getValue());
					System.out.println("cookie!");
				}
			}
		} else {
			user = AuthContainer.getUser(token);
			System.out.println(req.getParameter("else"));
		}
		if (user == null) {
			res.sendRedirect("/auth.html");
			return;
		}
		Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
		List<BlobKey> blobKeys = blobs.get("myFile");
		System.out.println(blobKeys);
		if (blobKeys == null || blobKeys.isEmpty()) {
			res.sendRedirect("/");
		} else {
			UserSavedFile file = new UserSavedFile();
			file.defineUser(user);
			file.setId(blobKeys.get(0).getKeyString());
			ofy().save().entity(file);
			res.sendRedirect("/FileProcessorServlet?blob-key=" + blobKeys.get(0).getKeyString());
		}

	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		System.out.println("GET");
		BlobKey blobKey = new BlobKey(req.getParameter("blob-key"));
		blobstoreService.serve(blobKey, res);
	}
}
