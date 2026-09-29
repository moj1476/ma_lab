package com.example.blog.mapper;

import com.example.blog.dto.CommentCreateDTO;
import com.example.blog.dto.CommentDTO;
import com.example.blog.dto.CommentUpdateDTO;
import com.example.blog.model.Comment;
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
public interface CommentMapper {

    @Mapping(target = "article", source = "articleId")
    @Mapping(target = "author", source = "authorId")
    Comment map(CommentCreateDTO dto);

    @Mapping(target = "articleId", source = "article.id")
    @Mapping(target = "authorId", source = "author.id")
    CommentDTO map(Comment model);

    @Mapping(target = "article", source = "articleId")
    @Mapping(target = "author", source = "authorId")
    void update(CommentUpdateDTO dto, @MappingTarget Comment model);
}
