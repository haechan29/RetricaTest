### 앱 소개
`흑백`, `밝기`를 조절할 수 있는 간단한 필터 앱<br/><br/>
<img src="https://github.com/user-attachments/assets/d60e12f2-6f00-4e95-8d24-f78ca0db4eeb"/><br/><br/>

### 아키텍처
MVVM
  - UI와 사용자의 상호작용을 ViewModel으로 전달
  - ViewModel에서 data를 변경
  - Activity에서 UI State를 변경

### 필터 적용 방식
`ColorFilter` 사용
  - 흑백 필터는 채도를 조절
  - 밝기 필터는 기존의 RGB 값에 상수를 더해서 더 하얗게 보이게 함

<details>
  <summary>코드 확인하기</summary>
  
```kotlin
// 흑백 필터
private fun createGreyScaleColorFilter(saturation: Float): ColorMatrixColorFilter {
    val grayscaleMatrix = ColorMatrix().apply {
        setSaturation(saturation)
    }
    return ColorMatrixColorFilter(grayscaleMatrix)
}

// 밝기 필터
private fun createLuminosityColorFilter(luminosity: Float): ColorMatrixColorFilter {
    val scaled = luminosity * 255f * LUMINOSITY_FILTER_LIMIT
    val luminosityMatrix = ColorMatrix(
        floatArrayOf(
            1f, 0f, 0f, 0f, scaled,
            0f, 1f, 0f, 0f, scaled,
            0f, 0f, 1f, 0f, scaled,
            0f, 0f, 0f, 1f, 0f
        )
    )
    return ColorMatrixColorFilter(luminosityMatrix)
}
```
</details>

### 구현 중 궁금했던 사항
밝기 조절 필터는 어떤 방식으로 구현하는게 자연스러울지?
- RGB 값에 상수를 더하는 방식
- RGB 값에 상수를 곱하는 방식

### 필터 적용에 대한 생각
- 단순히 RGB 값을 변경하는 단순한 방법으로 이미지의 느낌이 달라진다는 점이 신기했음
- 이미지의 느낌에 맞는 필터를 선택하는 것은 항상 어려운 일이라고 느껴짐

### 코드 확장에 대한 생각
- 채도, 명도를 조절하기 위해 컬러 필터를 사용했지만 더 복잡한 기능은 OpenGL과 같은 라이브러리를 사용해야 할 것으로 예상
- OpenGL 사용시 필터에 따라 쉐이더 로직만 변경하고, 나머지 부분은 재활용 가능할 것으로 예상
- 셰이더 로직 (C++ 코드) 은 서버에서 다운받고 로컬 DB에 저장해서 사용하면 될 것으로 예상 (EncryptedSharedPreferences 등의 라이브러리 사용)

### 소요시간
| 내용 | 소요 시간 |
|:-----------|-----------:|
| 구현 방식 설계       | 2시간   |
| 커스텀뷰 (시크바) 구현       | 1시간 30분   |
| 화면 UI 구성       | 1시간   |
| UI 로직 구현       | 3시간   |
| UI 관련 데이터 로직 단순화       | 1시간 30분   |
| **총합**       | **9시간**   |
