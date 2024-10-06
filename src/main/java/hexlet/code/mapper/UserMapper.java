package hexlet.code.mapper;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/*@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)*/
@Mapper(
        // Подключение JsonNullableMapper
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
//@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public abstract class UserMapper {

    @Mapping(target = "id", source = "id")
    public abstract UserDTO mapModel(User user);

    public abstract User map(UserCreateDTO dto);

    //@Mapping(target = "email", source = "email")
    //public abstract User map(UserDTO dto, @MappingTarget User user);
    public abstract User map(UserDTO dto, @MappingTarget User user);

    public abstract void update(UserUpdateDTO dto, @MappingTarget User user);

    //public abstract void toEntity(UserDTO dto, @MappingTarget User user);


}
