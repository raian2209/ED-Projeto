import React, { useState, useRef } from "react";
import {
  Upload,
  FileArchive,
  Download,
  X,
  CheckCircle2,
  ArrowRight,
  RefreshCw,
  Settings,
  AlertCircle,
  FileIcon,
} from "lucide-react";
import { compressFile, decompressFile } from "./service/huffmanService";
import { Card } from "./components/ui/card";
import { Button } from "./components/ui/button";
import { Progress } from "./components/ui/progress";

type ProcessingMode = "compress" | "decompress";
type AppStatus = "idle" | "processing" | "completed" | "error";

export default function App() {
  const [mode, setMode] = useState<ProcessingMode>("compress");
  const [file, setFile] = useState<File | null>(null);
  const [status, setStatus] = useState<AppStatus>("idle");
  const [progress, setProgress] = useState<number>(0);
  const [resultUrl, setResultUrl] = useState<string | null>(null);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [dragActive, setDragActive] = useState<boolean>(false);

  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleDrag = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === "dragenter" || e.type === "dragover") {
      setDragActive(true);
    } else if (e.type === "dragleave") {
      setDragActive(false);
    }
  };

  const handleDrop = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      handleFileSelect(e.dataTransfer.files[0]);
    }
  };

  const handleFileSelect = (selectedFile: File) => {
    if (status === "processing") return;
    setFile(selectedFile);
    setStatus("idle");
    setProgress(0);
    setResultUrl(null);
    setErrorMessage(null);
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      handleFileSelect(e.target.files[0]);
    }
  };

  const processFile = async () => {
    if (!file) return;

    setStatus("processing");
    setErrorMessage(null);
    setProgress(10);

    const progressInterval = setInterval(() => {
      setProgress((prev) => {
        if (prev >= 90) return prev;
        return prev + 5;
      });
    }, 500);

    try {
      let blob: Blob;
      if (mode === "compress") {
        blob = await compressFile(file);
      } else {
        blob = await decompressFile(file);
      }

      clearInterval(progressInterval);
      setProgress(100);

      const url = window.URL.createObjectURL(blob);
      setResultUrl(url);
      setStatus("completed");
    } catch (error) {
      clearInterval(progressInterval);
      setProgress(0);
      setStatus("error");
      console.error("Erro na operação:", error);
      setErrorMessage(
        "Falha ao conectar com o servidor. Verifique se o backend está rodando em localhost:8080."
      );
    }
  };

  const reset = () => {
    setFile(null);
    setStatus("idle");
    setProgress(0);
    setResultUrl(null);
    setErrorMessage(null);
    if (fileInputRef.current) fileInputRef.current.value = "";
  };

  const formatBytes = (bytes: number, decimals: number = 2): string => {
    if (!+bytes) return "0 Bytes";
    const k = 1024;
    const dm = decimals < 0 ? 0 : decimals;
    const sizes = ["Bytes", "KB", "MB", "GB"];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return `${parseFloat((bytes / Math.pow(k, i)).toFixed(dm))} ${sizes[i]}`;
  };

  return (
    <div className="min-h-screen bg-slate-50 flex items-center justify-center p-4 font-sans text-slate-900">
      <div className="w-full max-w-xl space-y-8">
        <div className="text-center space-y-2">
          <div className="inline-flex items-center justify-center p-3 bg-white rounded-2xl shadow-sm border border-slate-200 mb-4">
            <FileArchive className="w-8 h-8 text-indigo-600" />
          </div>
          <h1 className="text-3xl font-bold tracking-tight text-slate-900">
            Huffman Turbo Compress
          </h1>
          <p className="text-slate-500 max-w-md mx-auto">
            Serviço seguro e rápido para otimizar seus arquivos. Integração
            direta com nosso cluster de processamento.
          </p>
        </div>

        <Card className="overflow-hidden bg-white/80 backdrop-blur-sm shadow-xl border-slate-200/60">
          <div className="grid grid-cols-2 border-b border-slate-100">
            <button
              onClick={() => {
                setMode("compress");
                reset();
              }}
              className={`p-4 text-sm font-medium transition-colors flex items-center justify-center gap-2 ${
                mode === "compress"
                  ? "bg-white text-indigo-600 border-b-2 border-indigo-600"
                  : "bg-slate-50/50 text-slate-600 hover:text-slate-900 hover:bg-slate-100"
              }`}
            >
              <FileArchive className="w-4 h-4" />
              Comprimir
            </button>
            <button
              onClick={() => {
                setMode("decompress");
                reset();
              }}
              className={`p-4 text-sm font-medium transition-colors flex items-center justify-center gap-2 ${
                mode === "decompress"
                  ? "bg-white text-indigo-600 border-b-2 border-indigo-600"
                  : "bg-slate-50/50 text-slate-600 hover:text-slate-900 hover:bg-slate-100"
              }`}
            >
              <RefreshCw className="w-4 h-4" />
              Descomprimir
            </button>
          </div>

          <div className="p-6 space-y-6">
            {!file ? (
              <div
                className={`relative border-2 border-dashed rounded-xl p-12 text-center transition-all duration-200 ease-in-out ${
                  dragActive
                    ? "border-indigo-500 bg-indigo-50/50"
                    : "border-slate-200 hover:border-slate-300 hover:bg-slate-50"
                }`}
                onDragEnter={handleDrag}
                onDragLeave={handleDrag}
                onDragOver={handleDrag}
                onDrop={handleDrop}
              >
                <input
                  ref={fileInputRef}
                  type="file"
                  className="hidden"
                  onChange={handleChange}
                  accept={
                    mode === "compress" ? "*" : ".zip,.rar,.7z,.tar,.gz,.huff"
                  }
                />
                <div className="flex flex-col items-center justify-center gap-4">
                  <div
                    className={`p-4 rounded-full ${
                      dragActive ? "bg-indigo-100" : "bg-slate-100"
                    }`}
                  >
                    <Upload
                      className={`w-8 h-8 ${
                        dragActive ? "text-indigo-600" : "text-slate-400"
                      }`}
                    />
                  </div>
                  <div className="space-y-1">
                    <p className="text-sm font-medium text-slate-900">
                      Clique para selecionar ou arraste um arquivo
                    </p>
                    <p className="text-xs text-slate-500">
                      {mode === "compress"
                        ? "Suporta qualquer formato de arquivo até 2GB"
                        : "Suporta arquivos .huff, .zip ou outros comprimidos"}
                    </p>
                  </div>
                  <Button
                    variant="secondary"
                    onClick={() => fileInputRef.current?.click()}
                  >
                    Selecionar Arquivo
                  </Button>
                </div>
              </div>
            ) : (
              <div className="space-y-6">
                <div className="flex items-center gap-4 p-4 rounded-lg border border-slate-100 bg-slate-50/50">
                  <div className="p-3 bg-white rounded-lg shadow-sm border border-slate-100">
                    <FileIcon className="w-6 h-6 text-indigo-600" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium text-slate-900 truncate">
                      {file.name}
                    </p>
                    <p className="text-xs text-slate-500">
                      {formatBytes(file.size)}
                    </p>
                  </div>
                  {status === "idle" || status === "error" ? (
                    <button
                      onClick={reset}
                      className="p-2 text-slate-400 hover:text-red-500 transition-colors"
                    >
                      <X className="w-5 h-5" />
                    </button>
                  ) : null}
                </div>

                {status === "processing" && (
                  <div className="space-y-2">
                    <div className="flex justify-between text-xs text-slate-500">
                      <span>Processando arquivo...</span>
                      <span>{progress}%</span>
                    </div>
                    <Progress value={progress} />
                  </div>
                )}

                {status === "error" && (
                  <div className="bg-red-50 border border-red-100 rounded-lg p-4 flex items-start gap-3">
                    <AlertCircle className="w-5 h-5 text-red-600 mt-0.5" />
                    <div className="space-y-1">
                      <p className="text-sm font-medium text-red-900">
                        Erro ao processar
                      </p>
                      <p className="text-sm text-red-700">
                        {errorMessage || "Ocorreu um erro desconhecido."}
                      </p>
                    </div>
                  </div>
                )}

                {status === "completed" && (
                  <div className="bg-green-50 border border-green-100 rounded-lg p-4 flex items-start gap-3">
                    <CheckCircle2 className="w-5 h-5 text-green-600 mt-0.5" />
                    <div className="space-y-1">
                      <p className="text-sm font-medium text-green-900">
                        Sucesso!
                      </p>
                      <p className="text-sm text-green-700">
                        Seu arquivo foi{" "}
                        {mode === "compress" ? "comprimido" : "descomprimido"}{" "}
                        com sucesso.
                      </p>
                    </div>
                  </div>
                )}

                <div className="flex gap-3 pt-2">
                  {(status === "idle" || status === "error") && (
                    <Button
                      className="w-full bg-indigo-600 hover:bg-indigo-700"
                      onClick={processFile}
                    >
                      {mode === "compress"
                        ? "Iniciar Compressão"
                        : "Iniciar Descompressão"}
                      <ArrowRight className="w-4 h-4 ml-2" />
                    </Button>
                  )}

                  {status === "completed" && (
                    <>
                      <Button
                        variant="outline"
                        className="flex-1"
                        onClick={reset}
                      >
                        Novo Arquivo
                      </Button>
                      <a
                        href={resultUrl || "#"}
                        download={
                          mode === "compress"
                            ? `${file.name}.huff`
                            : file.name.replace(".huff", "")
                        }
                        className="flex-1"
                      >
                        <Button className="w-full bg-indigo-600 hover:bg-indigo-700">
                          <Download className="w-4 h-4 mr-2" />
                          Baixar Arquivo
                        </Button>
                      </a>
                    </>
                  )}
                </div>
              </div>
            )}
          </div>

          <div className="bg-slate-50 px-6 py-4 border-t border-slate-100 flex items-center justify-between text-xs text-slate-500">
            <div className="flex items-center gap-2">
              <div
                className={`w-2 h-2 rounded-full ${
                  status === "processing"
                    ? "bg-yellow-400 animate-pulse"
                    : status === "error"
                    ? "bg-red-500"
                    : "bg-green-400"
                }`}
              />
              {status === "error" ? "Falha na conexão" : "Servidor Operacional"}
            </div>
            <div className="flex items-center gap-1 hover:text-slate-900 cursor-pointer transition-colors">
              <Settings className="w-3 h-3" />
              Configurações
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
}
