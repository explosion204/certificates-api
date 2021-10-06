package com.epam.esm.service;

import com.epam.esm.dto.TagDto;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.EntityAlreadyExistsException;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.InvalidEntityException;
import com.epam.esm.repository.PageContext;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.validator.TagValidator;
import com.epam.esm.validator.ValidationError;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.epam.esm.validator.ValidationError.INVALID_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {
    @InjectMocks
    private TagService tagService;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private TagValidator tagValidator;

    @BeforeAll
    static void setUp() {
        MockitoAnnotations.openMocks(TagServiceImplTest.class);
    }

    @Test
    void testFindAll() {
        PageContext pageContext = new PageContext();
        when(tagRepository.findAll(pageContext)).thenReturn(provideTagsList());

        List<TagDto> expectedDtoList = provideTagDtoList();
        List<TagDto> actualDtoList = tagService.findAll(pageContext);

        assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    void testFindById() {
        long tagId = 1;
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(provideTag()));

        TagDto expectedDto = provideTagDto();
        TagDto actualDto = tagService.findById(tagId);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    void testFindByIdWhenTagNotFound() {
        long tagId = 1;
        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tagService.findById(tagId));
    }

    @Test
    void testCreate() {
        TagDto tagDto = provideTagDto();
        Tag tag = provideTag();

        when(tagRepository.findByName(tagDto.getName())).thenReturn(Optional.empty());
        when(tagRepository.create(tag)).thenReturn(tag);

        tagService.create(tagDto);

        verify(tagValidator).validate(tag.getName());
        verify(tagRepository).findByName(tag.getName());
        verify(tagRepository).create(tag);
    }

    @Test
    void testCreateWhenTagInvalid() {
        String tagName = "";
        TagDto tagDto = provideTagDto();
        tagDto.setName(tagName);

        List<ValidationError> errorList = List.of(INVALID_NAME);
        when(tagValidator.validate(tagName)).thenReturn(errorList);

        assertThrows(InvalidEntityException.class, () -> tagService.create(tagDto));
    }

    @Test
    void testCreateWhenTagAlreadyExists() {
        TagDto tagDto = provideTagDto();
        Tag tag = provideTag();

        when(tagRepository.findByName(tagDto.getName())).thenReturn(Optional.of(tag));
        assertThrows(EntityAlreadyExistsException.class, () -> tagService.create(tagDto));

        verify(tagValidator).validate(tag.getName());
    }

    @Test
    void testDelete() {
        Tag tag = provideTag();
        int tagId = 1;
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        tagService.delete(tagId);

        verify(tagRepository).delete(tag);
    }

    @Test
    void testDeleteWhenTagNotFound() {
        int tagId = 1;
        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tagService.delete(tagId));
    }

    private Tag provideTag() {
        Tag tag = new Tag();
        tag.setId(1);
        tag.setName("tag");

        return tag;
    }

    private TagDto provideTagDto() {
        TagDto tagDto = new TagDto();
        tagDto.setId(1);
        tagDto.setName("tag");

        return tagDto;
    }

    private List<Tag> provideTagsList() {
        Tag firstTag = new Tag();
        firstTag.setId(1);
        firstTag.setName("tag1");

        Tag secondTag = new Tag();
        secondTag.setName("tag2");

        Tag thirdTag = new Tag();
        thirdTag.setName("tag3");

        return new ArrayList<>() {{
            add(firstTag);
            add(secondTag);
            add(thirdTag);
        }};
    }

    private List<TagDto> provideTagDtoList() {
        TagDto firstTagDto = new TagDto();
        firstTagDto.setId(1);
        firstTagDto.setName("tag1");

        TagDto secondTagDto = new TagDto();
        secondTagDto.setName("tag2");

        TagDto thirdTagDto = new TagDto();
        thirdTagDto.setName("tag3");

        return new ArrayList<>() {{
            add(firstTagDto);
            add(secondTagDto);
            add(thirdTagDto);
        }};
    }
}
