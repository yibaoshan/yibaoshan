package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Stack;

public class Easy_232_用栈实现队列 {

    /**
     * 请你仅使用两个栈实现先入先出队列。队列应当支持一般队列支持的所有操作（push、pop、peek、empty）：
     * <p>
     * 实现 MyQueue 类：
     * <p>
     * void push(int x) 将元素 x 推到队列的末尾
     * int pop() 从队列的开头移除并返回元素
     * int peek() 返回队列开头的元素
     * boolean empty() 如果队列为空，返回 true ；否则，返回 false
     *  
     * <p>
     * 说明：
     * <p>
     * 你只能使用标准的栈操作 —— 也就是只有 push to top, peek/pop from top, size, 和 is empty 操作是合法的。
     * 你所使用的语言也许不支持栈。你可以使用 list 或者 deque（双端队列）来模拟一个栈，只要是标准的栈操作即可。
     *  
     * <p>
     * 进阶：
     * <p>
     * 你能否实现每个操作均摊时间复杂度为 O(1) 的队列？换句话说，执行 n 个操作的总时间复杂度为 O(n) ，即使其中一个操作可能花费较长时间。
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/implement-queue-using-stacks
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        MyQueue obj = new MyQueue();
        obj.push(1);
        obj.push(2);
        obj.push(3);
        obj.push(4);
        obj.push(5);
        while (!obj.empty()) {
            System.out.println(obj.pop());
        }
    }

    /**
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：36.3 MB, 在所有 Java 提交中击败了42.22%的用户
     */
    class MyQueue {

        Stack<Integer> stack1;
        Stack<Integer> stack2;

        public MyQueue() {
            stack1 = new Stack<>();
            stack2 = new Stack<>();
        }

        public void push(int x) {
            stack1.push(x);
        }

        public int pop() {
            transfer();
            return stack2.pop();
        }

        public int peek() {
            transfer();
            return stack2.peek();
        }

        public boolean empty() {
            return stack1.empty() && stack2.empty();
        }

        private void transfer() {
            if (!stack1.empty() && stack2.empty()) {
                while (!stack1.empty()) stack2.push(stack1.pop());
            }
        }

    }


}
