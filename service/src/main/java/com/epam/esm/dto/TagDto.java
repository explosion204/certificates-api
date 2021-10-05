package com.epam.esm.dto;

import com.epam.esm.entity.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class TagDto extends IdentifiableDto<TagDto> {
    private long id;
    private String name;

    public Tag toTag() {
        Tag tag = new Tag();

        tag.setId(id);
        tag.setName(name);

        return tag;
    }

    public static TagDto fromTag(Tag tag) {
        TagDto tagDto = new TagDto();

        tagDto.setId(tag.getId());
        tagDto.setName(tag.getName());

        return tagDto;
    }
}
