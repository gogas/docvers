package servlets.documentServlet;

import beans.DocumentBean;
import beans.VersionBean;
import exception.BusinessException;
import exception.NoSuchObjectInDB;
import exception.ObjectAlreadyExistsException;
import exception.SystemException;
import service.DBOperations;
import service.RequestParser;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: alni
 * Date: 27.02.13
 * Time: 13:11
 * To change this template use File | Settings | File Templates.
 */
public class DeleteDocumentServlet extends HttpServlet {
    private DBOperations operations = DBOperations.getInstance();

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String docName = request.getParameter("docDel");
            operations.deleteDocument(RequestParser.getInstance().getAuthorBean(request).getLogin(), docName);
            showMessage(request, response);
        } catch (SystemException e) {
            System.out.println(e.getCause() + e.getMessage());
            throw new ServletException(e);
        } catch (BusinessException e) {
            if (e.getClass() == NoSuchObjectInDB.class) {
                request.setAttribute("docmessage", "This document has already been removed.");
                RequestDispatcher reqDispatcher = getServletConfig().getServletContext().getRequestDispatcher("/AllDocuments");
                reqDispatcher.forward(request, response);
            } else {
                throw new ServletException(e);
            }
        }
    }

    private void showMessage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher reqDispatcher = getServletConfig().getServletContext().getRequestDispatcher("/GetAllDocuments");
        reqDispatcher.forward(request, response);

    }
}
