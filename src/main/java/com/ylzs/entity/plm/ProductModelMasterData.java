package com.ylzs.entity.plm;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 9:56 2020/3/12
 */
@Data
@TableName(value = "capp_pi_product_model_master_data")
public class ProductModelMasterData implements Serializable {
    private static final long serialVersionUID = -8110990803136286868L;
    private Integer id;
    private Boolean affectCraft; //是否影响工艺
    private String brand;//品牌
    private String year;//年季
    private String department;//系列
    private String band;//季节
    private String styleName;//款式名称
    private String code;//款式编码
    private String productName;//商品名称
    private String occasion;//场合
    private String manner;//风格
    private String shape;//廊形
    private String channel;//市场渠道
    @TableField(value = "EEKA1_class")
    private String EEKA1Class;//赢家原来大类
    @TableField(value = "EEKA2_class")
    private String EEKA2Class;//赢家原来小类
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date southShopDate;//南方上市日期
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date northShopDate;//北方上市日期
    @TableField(value = "GB")
    private String GB;//执行标准
    @TableField(value = "STC")
    private String STC;//安全技术级别
    private String manufactureArea;//产地
    private String grade;//等级
    private String materialCode;//面料号
    private String materialComposition;//面料成分
    private String designer;//设计师
    private String grandCategory;//款式大类
    private String midCategory;//款式中类
    private String smallCategory;//款式小类
    private String zipPosition;//拉链位置
    private BigDecimal length;//长度
    private String pocketType;//侧袋类型
    private String retailPrice;//零售价
    private String designImage;//设计图片
    private String styleType;//款式智库类型
    @TableField(value = "imageURL")
    private String imageURL;//设计图片
    private String unit;//单位
    private String collar;//领型
    private String sleeve;//肩袖型
    private String cuff;//袖口型
    private String malibu;//里布形式
    private String dege;//袋盖
    private String belt;//腰带
    private String trouserLoop;//耳袢
    private String pocket;//口袋型
    private String wayOfTakingOff;//拉链型
    private String waistBand;//腰头型
    private String stitch;//外观线迹
    private String decorate;//装饰细节
    private String patterning;//图案装饰
    private String footMouth;//脚口结构
    private String clothingLengthGrade;//衣长等级
    private String hem;//下摆型
    private String sleeveLengthGrade;//袖长等级
    private String sleeveCutPiecesNumber;//袖片数
    private String currencyGrade;//通用级别
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date receiveTime;//接收时间
    @TableField(exist = false)
    private List<ProductModelEBomData> eBom;
    @TableField(exist = false)
    private List<ProductModelTuAnDefaultPart> tuAnDefaultParts;
}
