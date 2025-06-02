package com.ssafy.musoonzup.global.interceptor;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.time.LocalDateTime;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

@Intercepts({
    @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class TimestampInterceptor implements Interceptor {

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    StatementHandler handler = (StatementHandler) invocation.getTarget();
    Object paramObj = handler.getBoundSql().getParameterObject();

    if (paramObj == null) {
      return invocation.proceed();
    }

    String sql = handler.getBoundSql().getSql().toLowerCase();

    if (sql.startsWith("insert")) {
      setField(paramObj, "createdAt", LocalDateTime.now());
      setField(paramObj, "updatedAt", LocalDateTime.now());
    } else if (sql.startsWith("update")) {
      setField(paramObj, "updatedAt", LocalDateTime.now());
    }

    return invocation.proceed();
  }

  private void setField(Object target, String fieldName, Object value) {
    try {
      Field field = target.getClass().getSuperclass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(target, value);
    } catch (Exception e) {
      // 무시
    }
  }
}

