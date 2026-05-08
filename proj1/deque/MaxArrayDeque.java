package deque;
import java.util.Comparator;

// 继承 ArrayDeque，直接复用所有方法！不用复制代码
public class MaxArrayDeque<T> extends ArrayDeque<T> {

    // 保存构造时传入的默认比较器
    private Comparator<T> defaultComparator;

    // 构造方法：传入比较器
    public MaxArrayDeque(Comparator<T> c) {
        defaultComparator = c;
    }

    // 使用默认比较器找最大值
    public T max() {
        return max(defaultComparator);
    }

    // 使用传入的比较器找最大值
    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }

        T maxItem = get(0); // 先假设第一个是最大

        // 遍历所有元素，一个个比
        for (int i = 1; i < size(); i++) {
            T current = get(i);

            // 用比较器比较：current 比 maxItem 大就替换
            if (c.compare(current, maxItem) > 0) {
                maxItem = current;
            }
        }

        return maxItem;
    }
}