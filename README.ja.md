
# 🇯🇵 YomiYomi - 日本語学習アプリ

> **音声認識とTTS機能を搭載したインタラクティブ日本語学習アプリ**

YomiYomiは漢字、単語、文章、段落学習のための総合的なAndroidアプリケーションです。音声認識とTTS（Text-to-Speech）技術を活用して、視覚的・聴覚的学習の両方をサポートするインタラクティブな学習体験を提供します。

## ✨ 主要機能

### 🎯 基礎学習
- **漢字・単語リスト**: 体系的な漢字と単語データベースの探索
- **ランダム学習カード**: ランダムに提示される漢字・単語による反復学習
- **クイズモード**: 実力テストのための多様なクイズ形式

### 📚 個人化学習管理
- **マイ漢字帳・単語帳**: 個人向けカスタマイズ学習コレクションの作成・管理
- **文章・段落学習**: 実践的な日本語文脈理解のための長文テキスト学習
- **学習進度追跡**: 個人別学習状況と達成度管理

### 🎙️ 音声ベース学習
- **音声認識クイズ**: 実際の発音によるインタラクティブクイズ
- **TTS発音聞き取り**: 正確な日本語発音学習サポート
- **穴埋め問題**: 音声で答える文章・段落完成クイズ

### 📱 便利機能
- **ホームウィジェット**: 学習ストリーク追跡のためのホーム画面ウィジェット
- **多様な学習モード**: ユーザーの好みに合わせた学習方式選択

## 🛠 技術スタック

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

## 🏗 プロジェクトアーキテクチャ

### Clean Architecture 3層構造
```
📦 com.lass.yomiyomi
├── 📂 ui（表示レイヤー）
│   ├── 📂 screen        # 画面ごとのComposable
│   ├── 📂 component     # 再利用可能なUIコンポーネント
│   ├── 📂 theme         # Material Designテーマ
│   └── 📂 state         # UI状態管理
├── 📂 domain（ドメインレイヤー）
│   ├── 📂 model         # ドメインエンティティ
│   └── 📂 usecase       # ビジネスロジック
├── 📂 data（データレイヤー）
│   ├── 📂 database      # Roomデータベース
│   ├── 📂 dao           # DAO（データアクセスオブジェクト）
│   ├── 📂 repository    # リポジトリ実装
│   └── 📂 model         # データモデル
├── 📂 di                # 依存性注入
├── 📂 speech            # 音声認識・TTS管理
├── 📂 util              # ユーティリティクラス
└── 📂 widget            # ホーム画面ウィジェット   
```

## 🎯 主要技術的特徴

### 🎤 音声処理システム
- **リアルタイム音声認識**: Android SpeechRecognizerを活用した正確な日本語音声認識
- **インテリジェントテキスト正規化**: ふりがな除去、句読点処理など日本語特化前処理
- **ライフサイクル認識TTS**: アプリバックグラウンド切り替え時の自動音声停止などスマートな生命周期管理

### 🔄 効率的な状態管理
- **StateFlowベース**: リアクティブUI更新のための現代的状態管理
- **Navigation-Level TTS管理**: 画面遷移時の自動音声停止でユーザー体験向上
- **メモリ最適化**: 適切なスコープと生命周期管理によるメモリリーク防止

### 🎨 再利用可能なコンポーネント設計
- **UnifiedTTSButton**: 単一テキストと文章リスト両方に対応する汎用TTSボタン
- **Adaptive Layout**: テキスト長による動的レイアウト調整
- **Type-safe Navigation**: Enumベースの安全な画面遷移

## 📱 スクリーンショット

### メイン画面
- タブベースナビゲーションで構成された直感的なメインインターフェース
<p align="center">
  <img src="https://github.com/user-attachments/assets/20e8dc14-04cc-427c-91ef-e9610c2d6291" width="30%" />
  <img src="https://github.com/user-attachments/assets/f209b218-e803-4603-84c1-c136e407ddc4" width="30%" />
  <img src="https://github.com/user-attachments/assets/7b0ccfbf-b28f-4963-ae2d-b5b3503112b9" width="30%" />
</p>

### 漢字学習画面
- TTSボタンが統合された漢字カードビューと詳細情報表示およびクイズ画面
<p align="center">
  <img src="https://github.com/user-attachments/assets/04c4620a-5945-4f36-a066-9be5ff4a43e0" width="30%" />
  <img src="https://github.com/user-attachments/assets/a82ca113-e6dd-43b4-b34e-b955d190270f" width="30%" />
  <img src="https://github.com/user-attachments/assets/c315b4f7-09e8-406b-bcec-d0ce2851d8f9" width="30%" />
</p>

### 単語学習画面
- TTSボタンが統合された単語カードビューと詳細情報表示およびクイズ画面
<p align="center">
  <img src="https://github.com/user-attachments/assets/fcf91d68-b7c0-4c62-836e-ddd96f73b7fe" width="30%" />
  <img src="https://github.com/user-attachments/assets/5a79690f-aa07-414e-adfb-625dd32f8eaa" width="30%" />
  <img src="https://github.com/user-attachments/assets/7933e826-3334-4303-ab2b-d07dee735d4e" width="30%" />
</p>

### 文章学習画面
- 音声認識による会話クイズ
<p align="center">
  <img src="https://github.com/user-attachments/assets/50a1b5f5-7769-4e60-b580-ccf8e46358cf" width="30%" />
  <img src="https://github.com/user-attachments/assets/2e09abc6-76e7-4934-b5f9-f0f47340f64b" width="30%" />
  <img src="https://github.com/user-attachments/assets/d1b40785-890e-4eb3-bcf6-7440c185f3c4" width="30%" />
</p>

### 段落学習画面
- 段落リストと詳細画面およびクイズ画面
<p align="center">
  <img src="https://github.com/user-attachments/assets/83dcc13e-27f3-4fbb-b74a-9baa14f5fa22" width="30%" />
  <img src="https://github.com/user-attachments/assets/fac5d8d1-68c9-4145-8afb-fa6a53b73631" width="30%" />
  <img src="https://github.com/user-attachments/assets/8108b6b0-877d-4247-a5d2-d60574dc4fe6" width="30%" />
</p>

