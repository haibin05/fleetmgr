package frontend;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Created by gongy on 2015/11/2.
 */
@WebServlet(name = "version",
        urlPatterns = {"/version/build"}
)
public class VersionServlet extends javax.servlet.http.HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (InputStream inputStream = req.getServletContext().getResourceAsStream("/META-INF/MANIFEST.MF");) {
            Manifest manifest = new Manifest(inputStream);
            Attributes attributes = manifest.getMainAttributes();
            String version = attributes.getValue("Implementation-Build");
            resp.getWriter().print(version);
        }
    }

}
