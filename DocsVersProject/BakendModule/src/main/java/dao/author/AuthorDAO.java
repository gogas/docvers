package dao.author;

import dao.DAO;
import entities.Author;
import exception.DAOException;
import exception.MyException;
import exception.SystemException;

/**
 * Created with IntelliJ IDEA.
 * User: alni
 * Date: 12.02.13
 * Time: 9:05
 * To change this template use File | Settings | File Templates.
 */
public interface AuthorDAO extends DAO {
    public Author getAuthorByID(long id) throws MyException;

    public Author getAuthorByLogin(String login) throws MyException;
}
