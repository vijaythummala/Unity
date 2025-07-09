package com.unity.payment_service.mapper;

import com.unity.payment_service.dto.PaymentInfoDTO;
import com.unity.payment_service.entity.PaymentInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PaymentInfoMapper {

    PaymentInfoMapper INSTANCE = Mappers.getMapper(PaymentInfoMapper.class);

    PaymentInfo toEntity(PaymentInfoDTO dto);

    PaymentInfoDTO toDto(PaymentInfo entity);
}
