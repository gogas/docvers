package dao.version;

import dao.ExceptionsThrower;
import entities.Version;
import exception.*;
import org.h2.constant.ErrorCode;
import service.QueriesSQL;

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
 * Date: 12.02.13
 * Time: 9:11
 * To change this template use File | Settings | File Templates.
 */
public class VersionDAOImpl implements VersionDAO {
    private Connection conn;

    public VersionDAOImpl(Connection conn) throws MyException {
        if (conn == null)
            throw new NullConnectionException();
        this.conn = conn;
    }

    @Override
    public List<Version> getVersionsOfDocument(long id) throws MyException {
        List<Version> versions = new ArrayList<Version>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Version version = null;

        try {
            ps = conn.prepareStatement(QueriesSQL.SELECT_FROM_VERSION_WHERE_DOCUMENT_ID);
            ps.setLong(1, id);
            rs = ps.executeQuery();
            while (rs.next()) {
                version = new Version();
                version.setId(rs.getLong("ID"));
                version.setAuthorID(rs.getLong("AUTHOR_ID"));
                version.setDate(rs.getTimestamp("DATE"));
                version.setDocumentID(rs.getLong("DOCUMENT_ID"));
                version.setDocumentPath(rs.getString("DOCUMENT_PATH"));
                version.setVersionDescription(rs.getString("VERSION_DESCRIPTION"));
                version.setReleased(rs.getBoolean("is_released"));
                version.setVersionName(rs.getLong("version_name"));
                version.setVersionType(rs.getString("version_type"));
                versions.add(version);
            }
            if (versions == null) throw new NoSuchObjectInDB("Versions of this document");
            return versions;
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
    public void addVersion(Version version) throws MyException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (version == null) {
            throw new IllegalArgumentException();
        }
        try {
           // long name = getLastVersionNameInfo(version.getDocumentID()) + 1;
            //conn.commit();
            ps = conn.prepareStatement(QueriesSQL.UPDATE_VERSION_SET_IS_RELEASED);
            ps.setBoolean(1, true);
            ps.setLong(2, version.getDocumentID());
            ps.setBoolean(3, true);
            ps.executeUpdate();
            ps = conn.prepareStatement(QueriesSQL.INSERT_INTO_VERSION);
            ps.setLong(1, version.getDocumentID());
            ps.setLong(2, version.getAuthorID());
            ps.setTimestamp(3, version.getDate());
            ps.setString(4, version.getVersionDescription());
            ps.setString(5, version.getDocumentPath());
            ps.setBoolean(6, false);
            ps.setString(7, version.getVersionType());
            ps.setLong(8, version.getVersionName());
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
    public void deleteVersion(long versName, long docCode, String login) throws MyException {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(QueriesSQL.DELETE_FROM_VERSION_WHERE_VERSION_NAME_AND_DOC_AND_LOGIN);
            ps.setLong(1, versName);
            ps.setLong(2, docCode);
            ps.setString(3, login);
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

    @Override
    public String getVersionType(long versionName, long documentName, String login) throws MyException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(QueriesSQL.SELECT_VERSION_TYPE_FROM_VERSION);
            ps.setLong(1, versionName);
            ps.setLong(2, documentName);
            ps.setString(3, login);
            rs = ps.executeQuery();
            String type = null;
            if (rs.next()) {
                type = rs.getString("version_type");
            }

            return type;
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
    public Version getVersion(long id, long versName) throws MyException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Version version = null;

        try {
            ps = conn.prepareStatement(QueriesSQL.SELECT_FROM_VERSION_WHERE_DOCUMENT_ID_AND_VERSION_NAME);
            ps.setLong(1, id);
            ps.setLong(2, versName);
            rs = ps.executeQuery();
            while (rs.next()) {
                version = new Version();
                version.setId(rs.getLong("ID"));
                version.setAuthorID(rs.getLong("AUTHOR_ID"));
                version.setDate(rs.getTimestamp("DATE"));
                version.setDocumentID(rs.getLong("DOCUMENT_ID"));
                version.setDocumentPath(rs.getString("DOCUMENT_PATH"));
                version.setVersionDescription(rs.getString("VERSION_DESCRIPTION"));
                version.setReleased(rs.getBoolean("is_released"));
                version.setVersionName(rs.getLong("version_name"));
                version.setVersionType(rs.getString("version_type"));
            }
            if (version == null) throw new NoSuchObjectInDB("Version of this document with same name = " + versName);
            return version;
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
    public void updateVersionDescription(String login, long codeDocName, long versionName, String description) throws MyException {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(QueriesSQL.UPDATE_VERSION_DESCRIPTION);
            ps.setString(1, description);
            ps.setLong(3, codeDocName);
            ps.setLong(2, versionName);
            ps.setString(4, login);
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
    public long getLastVersionNameInfo(long docID) throws MyException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(QueriesSQL.SELECT_VERSION_NAME_FROM_VERSION);
            ps.setLong(1, docID);
            rs = ps.executeQuery();
            long name = 0;
            if (rs.next()) {
                name = rs.getLong("version_max_name");
            }
            return name;
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


}
