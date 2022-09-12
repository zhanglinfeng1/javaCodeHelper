package ${packagePath};

import ${packagePath}.${tableName};
import ${packagePath}.${tableName}VO;

import java.util.List;

/**
 * @Author: ${author}
 * @Date: ${dateTime}
*/
public interface ${tableName}Service{

    void insert${tableName}(String tenantId, ${tableName}VO obj);

    void update${tableName}(String tenantId, ${tableName}VO obj);

    ${tableName} get${tableName}(String tenantId, String id);

    int get${tableName}ListCount(String tenantId);

    List<${tableName}> get${tableName}List(String tenantId, int offset,int limit);
}
