package com.fruitfresh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fruitfresh.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 商品 Mapper
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 原子扣减库存并累加销量。
     * <p>
     * 条件里带 {@code stock >= #{quantity}}，由数据库保证并发下不会超卖；
     * 返回值 0 表示库存不足或商品已下架，调用方需按失败处理。
     *
     * @return 受影响行数（1 成功，0 失败）
     */
    @Update("""
            UPDATE t_product
            SET stock = stock - #{quantity},
                sales = sales + #{quantity},
                update_time = NOW()
            WHERE id = #{productId}
              AND deleted = 0
              AND status = 1
              AND stock >= #{quantity}
            """)
    int deductStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    /**
     * 原子回滚库存并扣减销量（订单取消时调用），销量最低扣到 0。
     */
    @Update("""
            UPDATE t_product
            SET stock = stock + #{quantity},
                sales = CASE WHEN sales >= #{quantity} THEN sales - #{quantity} ELSE 0 END,
                update_time = NOW()
            WHERE id = #{productId}
              AND deleted = 0
            """)
    int restoreStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
}
