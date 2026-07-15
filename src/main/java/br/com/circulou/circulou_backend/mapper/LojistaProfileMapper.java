package br.com.circulou.circulou_backend.mapper;

import br.com.circulou.circulou_backend.dto.LojistaProfileRequestDTO;
import br.com.circulou.circulou_backend.dto.LojistaProfileResponseDTO;
import br.com.circulou.circulou_backend.model.LojistaProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LojistaProfileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "statusPerfil", ignore = true)
    @Mapping(target = "statusDocumentacao", ignore = true)
    @Mapping(target = "ratingMedia", ignore = true)
    @Mapping(target = "lojas", ignore = true)
    LojistaProfile toEntity(LojistaProfileRequestDTO dto);

    @Mapping(source = "usuario.id", target = "usuarioId")
    LojistaProfileResponseDTO toResponseDTO(LojistaProfile entity);
}
