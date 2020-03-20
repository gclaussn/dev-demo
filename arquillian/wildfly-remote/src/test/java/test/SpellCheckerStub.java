package test;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

@WebServlet("/check")
public class SpellCheckerStub extends HttpServlet {

  private static final long serialVersionUID = 1L;

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    String text = req.getReader()
      .lines()
      .collect(Collectors.joining());

    res.setContentType(MediaType.TEXT_PLAIN);
    res.setStatus(Status.OK.getStatusCode());
    res.getWriter().write(text);
    res.getWriter().write(" (spell checked by stub)");
  }
}
