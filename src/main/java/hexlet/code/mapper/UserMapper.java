package hexlet.code.mapper;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.model.User;
//import org.mapstruct.*;
import org.mapstruct.*;

/*@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)*/
@Mapper(
        // Подключение JsonNullableMapper
        uses = { JsonNullableMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {
/*
    @Mapping(target = "category", source = "categoryId")
    public abstract Product map(ProductCreateDTO dto);

    @Mapping(target = "title", source = "name")
    public abstract void update(PostUpdateDTO dto, @MappingTarget Post model);
*/

    @Mapping(target = "id", source = "id")
    public abstract UserDTO mapModel(User user);

    public abstract User map(UserCreateDTO dto);

    public abstract void update(UserUpdateDTO dto, @MappingTarget User user);


}
