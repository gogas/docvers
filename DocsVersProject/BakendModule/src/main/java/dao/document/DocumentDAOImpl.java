package dao.document;

import entities.Document;
import exception.*;
import org.h2.constant.ErrorCode;
import service.QueriesSQL;
import dao.ExceptionsThrower;
import java.lang.IllegalArgumentException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: alni
 * Date: 07.02.13
 * Time: 11:03
 * To change this template use File | Settings | File Templates.
 */
public class DocumentDAOImpl implements DocumentDAO {
    private Connection conn;

    public DocumentDAOImpl(Connection conn) throws MyException {
        if (conn == null)
            throw new NullConnectionException();
        this.conn = conn;

    }

    @Override
    public Document getDocumentByAuthorAndName(String login, long docNameCode) throws MyException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Document doc = null;
        try {
            ps = conn.prepareStatement(QueriesSQL.SELECT_FROM_DOCUMENT_WHERE_DOCUMENT_NAME_CODE_AND_AUTHOR_ID);
            ps.setLong(1, docNameCode);
            ps.setString(2, login);
            rs = ps.executeQuery();
            doc = createDocumentFromResultSet(rs);
            if (doc == null)
                throw new NoSuchObjectInDB("There are no documents in database that matches your request.");
            return doc;
        } catch (SQLException e) {
            throw ExceptionsThrower.throwException(e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }
    }

    @Override
    public long getDocumentID(String login, long docName) throws MyException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(QueriesSQL.SELECT_ID_FROM_DOCUMENT);
            ps.setString(1, login);
            ps.setLong(2, docName);
            rs = ps.executeQuery();
            long id = 0;
            if (rs.next()) {
                id = rs.getLong("id");
            }
            if (id == 0) throw new NoSuchObjectInDB("There are no documents in database that matches your request.");
            return id;
        } catch (SQLException e) {
            throw ExceptionsThrower.throwException(e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }
    }

    @Override
    public void updateDocumentDescription(String login, long codeDocName, String description) throws MyException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(QueriesSQL.UPDATE_DOCUMENT_DESCRIPTION);
            ps.setString(1, description);
            ps.setLong(2, codeDocName);
            ps.setString(3, login);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw ExceptionsThrower.throwException(e);

        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }
    }

    @Override
    public void addDocument(Document document) throws MyException {
        PreparedStatement ps = null;
        if (document == null) {
            throw new IllegalArgumentException();
        }
        if (document.getDocumentName().length() > 20) {
            throw new exception.IllegalArgumentException("Too long name");
        }
        try {
            ps = conn.prepareStatement(QueriesSQL.INSERT_INTO_DOCUMENT_AUTHOR_NAME_DESCRIPTION_VALUES);
            ps.setLong(1, document.getAuthorID());
            ps.setString(2, document.getDocumentName());
            ps.setString(3, document.getDescription());
            ps.setLong(4, document.getCodeDocumentName());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw ExceptionsThrower.throwException(e);
        } finally {
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }

    }

    @Override
    public List<Document> getDocumentsByAuthorID(long id) throws MyException {

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(QueriesSQL.SELECT_FROM_DOCUMENT_WHERE_AUTHOR_ID);
            ps.setLong(1, id);
            rs = ps.executeQuery();
            return createDocumentsListFromResultSet(rs);
        } catch (SQLException e) {
            throw ExceptionsThrower.throwException(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }
    }

    @Override
    public void deleteDocument(String login, long docNameCode) throws MyException {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(QueriesSQL.DELETE_FROM_DOCUMENT_WHERE_AUTHOR_ID_AND_CODE);
            ps.setString(1, login);
            ps.setLong(2, docNameCode);
            int i = ps.executeUpdate();
            if (i == 0) throw new NoSuchObjectInDB("Nothing to delete");
        } catch (SQLException e) {
            throw ExceptionsThrower.throwException(e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }
    }

    private List<Document> createDocumentsListFromResultSet(ResultSet rs) throws MyException {
        List<Document> documents = new ArrayList<Document>();
        Document document = null;
        boolean flag = true;
        while (flag) {
            document = createDocumentFromResultSet(rs);
            if (document != null) {
                documents.add(document);
            } else flag = false;

        }
        if (documents.isEmpty())
            throw new NoSuchObjectInDB("There are no documents in database that matches your request.");
        else return documents;


    }

    private Document createDocumentFromResultSet(ResultSet rs) throws MyException {
        Document document = null;
        try {
            if (rs.next()) {
                document = new Document();
                document.setId(rs.getLong("ID"));
                document.setAuthorID(rs.getLong("AUTHOR_ID"));
                document.setDocumentName(rs.getString("DOCUMENT_NAME"));
                document.setDescription(rs.getString("DESCRIPTION"));
                document.setCodeDocumentName(rs.getLong("code_document_name"));
            }
            return document;
        } catch (SQLException e) {
            throw ExceptionsThrower.throwException(e);
        }

    }


}
