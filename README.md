# 🇯🇵 YomiYomi - 일본어 학습 앱

> **음성 인식과 TTS 기능을 탑재한 인터랙티브 일본어 학습 앱**

YomiYomi는 한자, 단어, 문장, 문단 학습을 위한 종합적인 안드로이드 애플리케이션입니다. 음성 인식과 TTS(Text-to-Speech) 기술을 활용하여 시각적, 청각적 학습을 모두 지원하는 인터랙티브한 학습 경험을 제공합니다.

## ✨ 주요 기능

### 🎯 기초 학습
- **한자/단어 목록**: 체계적인 한자와 단어 데이터베이스 탐색
- **랜덤 학습 카드**: 무작위로 제시되는 한자/단어로 반복 학습
- **퀴즈 모드**: 실력 테스트를 위한 다양한 퀴즈 형태

### 📚 개인화 학습 관리
- **나만의 한자장/단어장**: 개인 맞춤형 학습 컬렉션 생성 및 관리
- **문장/문단 학습**: 실전 일본어 문맥 이해를 위한 긴 텍스트 학습
- **학습 진도 추적**: 개인별 학습 현황 및 성취도 관리

### 🎙️ 음성 기반 학습
- **음성 인식 퀴즈**: 실제 발음을 통한 인터랙티브 퀴즈
- **TTS 발음 듣기**: 정확한 일본어 발음 학습 지원
- **빈칸 채우기**: 음성으로 답하는 문장/문단 완성 퀴즈

### 📱 편의 기능
- **홈 위젯**: 학습 스트릭 추적을 위한 홈 화면 위젯
- **다양한 학습 모드**: 사용자 선호에 맞는 학습 방식 선택

## 🛠 기술 스택

### Core Technologies
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Design System**: Material Design 3
- **Architecture**: Clean Architecture (Domain-Data-UI)
- **Pattern**: MVVM with Repository Pattern

### Libraries & Frameworks
- **Dependency Injection**: Dagger Hilt
- **Database**: Room Database with TypeConverters
- **Asynchronous**: Kotlin Coroutines, StateFlow
- **Navigation**: Compose Navigation with Type-safe Arguments
- **Speech**: Android SpeechRecognizer, TextToSpeech API

## 🏗 프로젝트 아키텍처

### Clean Architecture 3계층 구조
```
📦 com.lass.yomiyomi
├── 📂 ui (Presentation Layer)
│   ├── 📂 screen          # 화면별 Composable
│   ├── 📂 component       # 재사용 가능한 UI 컴포넌트
│   ├── 📂 theme          # Material Design 테마
│   └── 📂 state          # UI 상태 관리
├── 📂 domain (Domain Layer)
│   ├── 📂 model          # 도메인 엔티티
│   └── 📂 usecase        # 비즈니스 로직
├── 📂 data (Data Layer)
│   ├── 📂 database       # Room 데이터베이스
│   ├── 📂 dao            # Data Access Objects
│   ├── 📂 repository     # 데이터 저장소 구현
│   └── 📂 model          # 데이터 모델
├── 📂 di                 # Dependency Injection
├── 📂 speech             # 음성 인식/TTS 관리
├── 📂 util               # 유틸리티 클래스
└── 📂 widget             # 홈 화면 위젯
```

## 🎯 주요 기술적 특징

### 🎤 음성 처리 시스템
- **실시간 음성 인식**: Android SpeechRecognizer를 활용한 정확한 일본어 음성 인식
- **지능형 텍스트 정규화**: 후리가나 제거, 구두점 처리 등 일본어 특화 전처리
- **라이프사이클 인식 TTS**: 앱 백그라운드 전환 시 자동 음성 중지 등 스마트한 생명주기 관리

### 🔄 효율적인 상태 관리
- **StateFlow 기반**: 반응형 UI 업데이트를 위한 현대적 상태 관리
- **Navigation-Level TTS 관리**: 화면 전환 시 자동 음성 정지로 사용자 경험 향상
- **메모리 최적화**: 적절한 스코프와 생명주기 관리로 메모리 누수 방지

### 🎨 재사용 가능한 컴포넌트 설계
- **UnifiedTTSButton**: 단일 텍스트와 문장 리스트 모두 지원하는 범용 TTS 버튼
- **Adaptive Layout**: 텍스트 길이에 따른 동적 레이아웃 조정
- **Type-safe Navigation**: Enum 기반 안전한 화면 전환

## 📱 스크린샷

### 메인 화면
- 탭 기반 네비게이션으로 구성된 직관적인 메인 인터페이스
<p align="center">
  <img src="https://github.com/user-attachments/assets/20e8dc14-04cc-427c-91ef-e9610c2d6291" width="30%" />
  <img src="https://github.com/user-attachments/assets/f209b218-e803-4603-84c1-c136e407ddc4" width="30%" />
  <img src="https://github.com/user-attachments/assets/7b0ccfbf-b28f-4963-ae2d-b5b3503112b9" width="30%" />
</p>

### 한자 학습 화면
- TTS 버튼이 통합된 한자 카드 뷰와 상세 정보 표시 그리고 퀴즈 화면
<p align="center">
  <img src="https://github.com/user-attachments/assets/04c4620a-5945-4f36-a066-9be5ff4a43e0" width="30%" />
  <img src="https://github.com/user-attachments/assets/a82ca113-e6dd-43b4-b34e-b955d190270f" width="30%" />
  <img src="https://github.com/user-attachments/assets/c315b4f7-09e8-406b-bcec-d0ce2851d8f9" width="30%" />
</p>

### 단어 학습 화면
- TTS 버튼이 통합된 단어 카드 뷰와 상세 정보 표시 그리고 퀴즈 화면
<p align="center">
  <img src="https://github.com/user-attachments/assets/fcf91d68-b7c0-4c62-836e-ddd96f73b7fe" width="30%" />
  <img src="https://github.com/user-attachments/assets/5a79690f-aa07-414e-adfb-625dd32f8eaa" width="30%" />
  <img src="https://github.com/user-attachments/assets/7933e826-3334-4303-ab2b-d07dee735d4e" width="30%" />
</p>

### 문장 학습 화면
- 음성 인식을 통한 말하기 퀴즈
<p align="center">
  <img src="https://github.com/user-attachments/assets/50a1b5f5-7769-4e60-b580-ccf8e46358cf" width="30%" />
  <img src="https://github.com/user-attachments/assets/2e09abc6-76e7-4934-b5f9-f0f47340f64b" width="30%" />
  <img src="https://github.com/user-attachments/assets/d1b40785-890e-4eb3-bcf6-7440c185f3c4" width="30%" />
</p>

### 문단 학습 화면
- 문단 리스트와 상세 화면 그리고 퀴즈 화면
<p align="center">
  <img src="https://github.com/user-attachments/assets/83dcc13e-27f3-4fbb-b74a-9baa14f5fa22" width="30%" />
  <img src="https://github.com/user-attachments/assets/fac5d8d1-68c9-4145-8afb-fa6a53b73631" width="30%" />
  <img src="https://github.com/user-attachments/assets/8108b6b0-877d-4247-a5d2-d60574dc4fe6" width="30%" />
</p>
