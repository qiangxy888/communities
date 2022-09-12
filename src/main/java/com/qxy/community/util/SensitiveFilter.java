package com.qxy.community.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/9/9 7:22
 */
@Slf4j
@Component
public class SensitiveFilter {
    //替换符
    private static final String REPLACEMENT = "***";
    //根节点
    private TrieNode rootNode = new TrieNode();

    //初始化方法，当容器实例化当前类（调用它的构造器之后）该方法被自动调用，而在服务器启动时bean被初始化
    @PostConstruct
    public void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive_words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            //读文件
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                //将读到的字符串添加到前缀树
                addKeyword(keyword);
            }
        } catch (IOException e) {
            log.error("加载敏感词失败！" + e.getMessage());
        }
    }

    /**
     * 内部类，描述前缀树的某一个节点
     */
    private class TrieNode {
        //关键词结束标志
        private boolean isKeyWordEnd;

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        //当前节点的子节点子节点可能有多个，key--下级字符，value--下级节点
        Map<Character, TrieNode> subNodes = new HashMap<>();

        //添加子节点的方法
        public void addSubNodes(Character ch, TrieNode node) {
            subNodes.put(ch, node);
        }

        //获取子节点的方法
        public TrieNode getSubNode(Character ch) {
            return subNodes.get(ch);
        }
    }

    /**
     * 将一个敏感词添加到前缀树中
     *
     * @param keyword
     */
    private void addKeyword(String keyword) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char ch = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(ch);
            if (subNode == null) {
                //初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNodes(ch, subNode);
            }
            //指向子节点进入下一循环
            tempNode = subNode;
            //设置结束标志
            if (i == keyword.length() - 1) {
                tempNode.setKeyWordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     *
     * @param text 待过滤文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        //指针1，指向前缀树节点
        TrieNode tempNode = rootNode;
        //指针2，指向某段字符串起始位置
        int begin = 0;
        //指针3，指向遍历到的位置
        int position = 0;
        //存放结果
        StringBuilder sb = new StringBuilder();
        while (position < text.length()) {
            char c = text.charAt(position);
            //跳过符号
            if (isSymbol(c)) {
                //若指针1处于根节点，将此符号计入结果，指针2向下走一步
                if (tempNode == rootNode) {
                    sb.append(c);
                    begin++;
                }
                //无论符号在开头还是中间，指针2都往后走
                position++;
                continue;//进入下一轮循环
            }
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                //说明以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                //进入下一个位置
                position = ++begin;
                //重新指向根节点
                tempNode=rootNode;
            }else if(tempNode.isKeyWordEnd){
                //说明到了某个敏感词的结尾将begin-position字符串替换掉
                sb.append(REPLACEMENT);
                begin=++position;
                //重新指向根节点
                tempNode=rootNode;
            }else{
                //检查下一个字符
                position++;
            }

        }
        //将最后一批字符计入结果
        sb.append(text.substring(begin));
        return sb.toString();

    }

    /**
     * 判断是否为特殊符号
     *
     * @param c
     * @return true表示是特殊符号，false表示不是
     * CharUtils.isAsciiAlphanumeric(c)  判断为普通符号返回true，特殊符号返回false
     */
    //0x2E80-0x9FFF 是东亚文字范围
    private Boolean isSymbol(Character c) {
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }
}
