package com.bit.eduventure.payment.entity;

import com.bit.eduventure.payment.dto.ReceiptDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
//@Table: 테이블 이름등을 지정
@Table(name="T_Repceipt")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    int paymentId;  //결제 PK
    String productName;  //상품 이름
    int productPrice;   //상품 가격


    public ReceiptDTO EntityTODTO() {
        ReceiptDTO receiptDTO = ReceiptDTO.builder()
                .id(this.id)
                .paymentId(this.paymentId)
                .productName(this.productName)
                .productPrice(this.productPrice)
                .build();
        return receiptDTO;
    }

}
