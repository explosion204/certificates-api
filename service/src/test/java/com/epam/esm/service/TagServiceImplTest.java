package com.epam.esm.service;

import com.epam.esm.dto.TagDto;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.EntityAlreadyExistsException;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.InvalidEntityException;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.validator.TagValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(PER_CLASS)
class TagServiceImplTest {
    @InjectMocks
    private TagService tagService;

    @Mock
    private TagRepository tagRepository;

    @Spy
    private TagValidator tagValidator;

    @BeforeAll
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        when(tagRepository.findAll()).thenReturn(provideTagsList());

        List<TagDto> expectedDtoList = provideTagDtoList();
        List<TagDto> actualDtoList = tagService.findAll();

        assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    void testFindById() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(provideTag()));

        int tagId = 1;
        TagDto expectedDto = provideTagDto();
        TagDto actualDto = tagService.findById(tagId);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    void testFindByIdWhenTagNotFound() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.empty());

        int tagId = 1;
        assertThrows(EntityNotFoundException.class, () -> tagService.findById(tagId));
    }

    @Test
    void testCreate() {
        TagDto tagDto = provideTagDto();

        when(tagRepository.findByName(anyString())).thenReturn(Optional.empty());

        tagService.create(tagDto);

        int expectedInteractions = 1;
        verify(tagValidator, times(expectedInteractions)).validate(anyString());
        verify(tagRepository, times(expectedInteractions)).findByName(anyString());
        verify(tagRepository, times(expectedInteractions)).create(any(Tag.class));
    }

    @Test
    void testCreateWhenTagInvalid() {
        TagDto tagDto = provideTagDto();
        tagDto.setName("");

        assertThrows(InvalidEntityException.class, () -> tagService.create(tagDto));
    }

    @Test
    void testCreateWhenTagAlreadyExists() {
        TagDto tagDto = provideTagDto();

        when(tagRepository.findByName(anyString())).thenReturn(Optional.of(provideTag()));
        assertThrows(EntityAlreadyExistsException.class, () -> tagService.create(tagDto));

        int expectedInteractions = 1;
        verify(tagValidator, times(expectedInteractions)).validate(anyString());
    }

    @Test
    void testDelete() {
        when(tagRepository.delete(anyLong())).thenReturn(true);

        int tagId = 1;
        tagService.delete(tagId);

        int expectedInteractions = 1;
        verify(tagRepository, times(expectedInteractions)).delete(anyLong());
    }

    @Test
    void testDeleteWhenTagNotFound() {
        when(tagRepository.delete(anyLong())).thenReturn(false);

        int tagId = 1;
        assertThrows(EntityNotFoundException.class, () -> tagService.delete(tagId));

        int expectedInteractions = 1;
        verify(tagRepository, times(expectedInteractions)).delete(anyLong());
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
