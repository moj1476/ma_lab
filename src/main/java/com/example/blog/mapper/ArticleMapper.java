package com.example.blog.mapper;

import com.example.blog.dto.ArticleCreateDTO;
import com.example.blog.dto.ArticleDTO;
import com.example.blog.dto.ArticleUpdateDTO;
import com.example.blog.model.Article;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = ReferenceMapper.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ArticleMapper {

    @Mapping(target = "author", source = "authorId")
    Article map(ArticleCreateDTO dto);

    @Mapping(target = "authorId", source = "author.id")
    ArticleDTO map(Article model);

    @Mapping(target = "author", source = "authorId")
    void update(ArticleUpdateDTO dto, @MappingTarget Article model);
}
