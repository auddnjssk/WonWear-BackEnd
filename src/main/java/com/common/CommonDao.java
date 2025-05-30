package com.common;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CommonDao {

    private final SqlSession sqlSession;

    @Autowired
    public CommonDao(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    public <T> T selectOne(String queryId, Object parameter) {
        return sqlSession.selectOne(queryId, parameter);
    }
    public <E> List<E> selectList(String queryId, Object parameter) {
        return sqlSession.selectList(queryId, parameter);
    }
    public int insert(String queryId, Object parameter) {
        return sqlSession.insert(queryId, parameter);
    }
    public int update(String queryId, Object parameter) {
        return sqlSession.update(queryId, parameter);
    }
    public int delete(String queryId, Object parameter) {
        return sqlSession.delete(queryId, parameter);
    }
}