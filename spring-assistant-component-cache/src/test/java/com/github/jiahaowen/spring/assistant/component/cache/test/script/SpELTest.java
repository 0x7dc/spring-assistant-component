package com.github.jiahaowen.spring.assistant.component.cache.test.script;

import com.github.jiahaowen.spring.assistant.component.cache.script.AbstractScriptParser;
import com.github.jiahaowen.spring.assistant.component.cache.script.SpringELParser;
import com.github.jiahaowen.spring.assistant.component.cache.test.Simple;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;

/**
 * @author: jiahaowen
 */
public class SpELTest extends TestCase {

    AbstractScriptParser scriptParser=new SpringELParser();

    public void testJavaScript() throws Exception {

        String keySpEL="'test_'+#args[0]+'_'+#args[1]";

        Simple simple=new Simple();
        simple.setAge(18);
        simple.setName("刘德华");
        simple.setSex(0);
        Object[] arguments=new Object[]{"1111", "2222", simple};

        String res=scriptParser.getDefinedCacheKey(keySpEL, null, arguments, null, false);
        System.out.println(res);
        assertEquals("test_1111_2222", res);
        // 自定义函数使用
        Boolean rv=scriptParser.getElValue("#empty(#args[0])", null, arguments, Boolean.class);
        assertFalse(rv);

        String val=null;
        val=scriptParser.getElValue("#hash(#args[0])", null, arguments, String.class);
        System.out.println(val);
        assertEquals("1111", val);

        val=scriptParser.getElValue("#hash(#args[1])", null, arguments, String.class);
        System.out.println(val);
        assertEquals("2222", val);

        val=scriptParser.getElValue("#hash(#args[2])", null, arguments, String.class);
        System.out.println(val);
//        assertEquals("-290203482_-550943035_-57743508_-1052004462", val);

        val=scriptParser.getElValue("#hash(#args)", null, arguments, String.class);
        System.out.println(val);
//        assertEquals("322960956_-1607969343_673194431_1921252123", val);
    }

    public void testReturnIsMapWithHfield() throws Exception {

        String keySpEL="#retVal.get('rid')";
        Object[] arguments=new Object[]{"1111", "2222"};
        Map returnObj=new HashMap(1);
        returnObj.put("rid", "iamrid");
        String res=scriptParser.getDefinedCacheKey(keySpEL, null, arguments, returnObj, true);
        System.out.println(res);

        assertEquals("iamrid", res);

        Simple simple=new Simple();
        simple.setAge(18);
        simple.setName("刘德华");
        simple.setSex(0);
        keySpEL="#retVal.name";

        res=scriptParser.getDefinedCacheKey(keySpEL, null, arguments, simple, true);
        System.out.println(res);
        assertEquals("刘德华", res);

        // 自定义函数使用
        Boolean rv=scriptParser.getElValue("#empty(#args[0])", null, arguments, Boolean.class);
        assertFalse(rv);
    }
}
