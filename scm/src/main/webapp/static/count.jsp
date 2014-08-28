<%@ page import="com.ejushang.steward.common.genericdao.dao.hibernate.GeneralDAO" %>
<%@ page import="com.ejushang.steward.common.Application" %>
<%@ page import="org.hibernate.SQLQuery" %>
<%@ page import="java.util.List" %>
<%--
  User: Sed.Li
  Date: 14-8-20
  Time: 下午3:52
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>
<table style="border:solid #000000 1px;" border="1">
    <tr><td>类目名称</td><td>A</td><td>B</td><td>C</td><td>小计</td></tr>
<%
    GeneralDAO generalDAO = (GeneralDAO) Application.getBean("generalDAO");
    SQLQuery query = generalDAO.getSession().createSQLQuery("select  c.name as '类目名',(select count(pa.id) from t_product pa where pa.category_id=c.id and pa.deleted=0 and pa.style=\"A\") as A , " +
            "(select count(pb.id) from t_product pb where pb.category_id=c.id and pb.deleted=0 and  pb.style=\"B\") as B ," +
            "(select count(pc.id) from t_product pc where pc.category_id=c.id and pc.deleted=0 and pc.style=\"C\") as C ,count(p.id) as '小计' " +
            "from t_product p left join t_product_category c on p.category_id=c.id where p.deleted=0 group by c.id;") ;
    List<Object[]> list = query.list();
    for(Object[] objs:list){

%><tr> <%

        for(Object obj:objs){

     %>
    <td><%=obj%></td>
    <%
        }

%></tr><%

    }


%>
</table>

</body>
</html>