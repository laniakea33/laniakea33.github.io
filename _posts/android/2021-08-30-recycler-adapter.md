---
title: "getBindingAdapterPosition vs getAbsoluteAdapterPosition"
categories:
    - Android
---

## getBindingAdapterPosition vs getAbsoluteAdapterPosition

한 RecyclerView에 다양한 View Type을 모두 바인딩 하려면 기존 하나의 Adapter에서 position별로 데이터를 체크하여 타입별 별도 로직을 두어야 했다. 이렇게 하면 Adapter에 여러 타입의 바인딩 로직이 짬뽕되게 되어 추후 재사용가능성이 낮고, 복잡도가 증가하는 문제가 있다.

그래서 각 기능별로 별도 Adapter를 두어 ConcatAdapter를 통해 하나로 통합한 후 RecyclerView에 세팅하도록 지원한다.

이런 경우 각 아이템 ViewHolder에는 관점에따라 두가지 Position이 부여되는데, 하나는 RecyclerView전체에서 해당 아이템의 position이고, 다른 하나는 각 Adapter내부에서 해당 아이템의 position이다.

전자는 getAbsoluteAdapterPosition()을 통해, 후자는 getBindingAdapterPosition()를 통해 접근가능하다.