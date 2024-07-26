package org.chiu.micro.user.convertor;


import java.util.List;

import org.chiu.micro.user.dto.MenuDisplayDto;
import org.chiu.micro.user.dto.MenuWithChildDto;

public class MenuWithChildConvertor {

  private MenuWithChildConvertor() {
  }

  public static List<MenuWithChildDto> convert(List<MenuDisplayDto> displayDtos) {
    return displayDtos.stream().map(item -> MenuWithChildDto.builder()
        .menuId(item.getMenuId())
        .icon(item.getIcon())
        .name(item.getName())
        .status(item.getStatus())
        .title(item.getTitle())
        .status(item.getStatus())
        .title(item.getTitle())
        .url(item.getUrl())
        .parentId(item.getParentId())
        .orderNum(item.getOrderNum())
        .type(item.getType())
        .children(convert(item.getChildren()))
        .build())
        .toList();
  }

  public static MenuWithChildDto convert(MenuDisplayDto displayDto) {
    return MenuWithChildDto.builder()
        .menuId(displayDto.getMenuId())
        .icon(displayDto.getIcon())
        .name(displayDto.getName())
        .status(displayDto.getStatus())
        .title(displayDto.getTitle())
        .url(displayDto.getUrl())
        .parentId(displayDto.getParentId())
        .orderNum(displayDto.getOrderNum())
        .type(displayDto.getType())
        .children(convert(displayDto.getChildren()))
        .build();
  }

}
