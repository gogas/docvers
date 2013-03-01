package service;

import beans.AuthorBean;
import beans.DocumentBean;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 17.02.13
 * Time: 16:39
 * To change this template use File | Settings | File Templates.
 */
public class RequestParser {
    private static RequestParser instance;

    public static synchronized RequestParser getInstance() {
        if (instance == null) {
            instance = new RequestParser();
        }
        return instance;
    }

    public AuthorBean getAuthorBean(HttpServletRequest request) {
       // AuthorBean a = new AuthorBean("author2", "pass2");
        return (AuthorBean) request.getSession().getAttribute("user");

    }

    public DocumentBean getDocumentBean(HttpServletRequest request) {
        AuthorBean author = getAuthorBean(request);
        String name = request.getParameter("docname");
        String description = request.getParameter("docdescription");
        return new DocumentBean(author, name, description);
    }
}