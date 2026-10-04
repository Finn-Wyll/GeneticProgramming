import React from 'react';
import { Upload, FileText, CheckCircle } from 'lucide-react';

interface FileUploadProps {
  label: string;
  onFileSelect: (file: File) => void;
  file: File | null;
}

export default function FileUpload({ label, onFileSelect, file }: FileUploadProps) {
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      onFileSelect(e.target.files[0]);
    }
  };

  return (
    <div className="space-y-2">
      <label className="text-sm font-medium text-gray-700">{label}</label>
      <div 
        className={`relative border-2 border-dashed rounded-lg p-4 transition-colors ${
          file ? 'border-green-200 bg-green-50' : 'border-gray-200 hover:border-blue-400 bg-gray-50'
        }`}
      >
        <input
          type="file"
          accept=".csv"
          onChange={handleChange}
          className="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
        />
        <div className="flex items-center gap-3">
          {file ? (
            <CheckCircle className="w-5 h-5 text-green-600" />
          ) : (
            <Upload className="w-5 h-5 text-gray-600" />
          )}
          <div className="flex-1 truncate">
            {file ? (
              <span className="text-sm text-green-700 font-medium">{file.name}</span>
            ) : (
              <span className="text-sm text-gray-600">Click or drag CSV file</span>
            )}
          </div>
          {file && <FileText className="w-4 h-4 text-green-400" />}
        </div>
      </div>
    </div>
  );
}
