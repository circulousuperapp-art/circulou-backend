package br.com.circulou.circulou_backend.mapper;

import br.com.circulou.circulou_backend.dto.MotoristaProfileRequestDTO;
import br.com.circulou.circulou_backend.dto.MotoristaProfileResponseDTO;
import br.com.circulou.circulou_backend.model.MotoristaProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MotoristaProfileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "statusPerfil", ignore = true)
    @Mapping(target = "statusDocumentacao", ignore = true)
    @Mapping(target = "ratingMedia", ignore = true)
    MotoristaProfile toEntity(MotoristaProfileRequestDTO dto);

    @Mapping(source = "usuario.id", target = "usuarioId")
    MotoristaProfileResponseDTO toResponseDTO(MotoristaProfile entity);
}
