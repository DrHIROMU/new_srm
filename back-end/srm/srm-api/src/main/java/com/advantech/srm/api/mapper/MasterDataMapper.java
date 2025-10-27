package com.advantech.srm.api.mapper;

import com.advantech.srm.api.config.MapStructConfig;
import com.advantech.srm.common.dto.master.PurchasingOrgDto;
import com.advantech.srm.persistence.entity.main.master.PurchasingOrgEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface MasterDataMapper {
    PurchasingOrgDto toPurchasingOrgDto(PurchasingOrgEntity entity);
    List<PurchasingOrgDto> toPurchasingOrgDtoList(List<PurchasingOrgEntity> entities);
}
