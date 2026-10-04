'use client';

import React, { useState } from 'react';
import GPConfigForm from '@/components/GPConfigForm';
import FileUpload from '@/components/FileUpload';
import ResultsDashboard from '@/components/ResultsDashboard';

export interface ClassificationResult {
  trainAccuracy: number;
  testAccuracy: number;
  fMeasure: number;
  runtimeMs: number;
  modelDescription: string;
  history: Array<{
    generation: number;
    bestFitness: number;
    averageFitness: number;
  }>;
}

export default function Home() {
  const [trainFile, setTrainFile] = useState<File | null>(null);
  const [testFile, setTestFile] = useState<File | null>(null);
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<ClassificationResult | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleTrain = async (params: any) => {
    if (!trainFile || !testFile) {
      setError('Please upload both training and test CSV files.');
      return;
    }

    setLoading(true);
    setError(null);
    setResult(null);

    const formData = new FormData();
    formData.append('trainFile', trainFile);
    formData.append('testFile', testFile);
    
    // Convert params to JSON blob as expected by @ModelAttribute or @RequestPart?
    // Wait, my controller uses @ModelAttribute which expects form fields.
    // Actually, @ModelAttribute maps flat parameters.
    Object.keys(params).forEach(key => {
      formData.append(key, params[key]);
    });

    try {
      const response = await fetch('http://localhost:8080/api/gp/train', {
        method: 'POST',
        body: formData,
      });

      if (!response.ok) {
        throw new Error(`Error: ${response.statusText}`);
      }

      const data = await response.json();
      console.log('Training Result:', data);
      setResult(data);
    } catch (err: any) {
      setError(err.message || 'An error occurred during training.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="min-h-screen bg-gray-50 p-4 md:p-8">
      <div className="mx-auto max-w-6xl space-y-8">
        <header className="text-center space-y-2">
          <h1 className="text-4xl font-bold tracking-tight text-gray-900">Genetic Programming Dashboard</h1>
          <p className="text-lg text-gray-600">Evolutionary algorithms for dataset classification</p>
        </header>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div className="lg:col-span-1 space-y-8">
            <section className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
              <h2 className="text-xl font-semibold mb-4 flex items-center gap-2 text-gray-900">
                1. Upload Datasets
              </h2>
              <div className="space-y-4">
                <FileUpload 
                  label="Training Set (CSV)" 
                  onFileSelect={setTrainFile} 
                  file={trainFile}
                />
                <FileUpload 
                  label="Test Set (CSV)" 
                  onFileSelect={setTestFile} 
                  file={testFile}
                />
              </div>
            </section>

            <section className="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
              <h2 className="text-xl font-semibold mb-4 text-gray-900">
                2. Configuration
              </h2>
              <GPConfigForm onTrain={handleTrain} loading={loading} />
            </section>
          </div>

          <div className="lg:col-span-2">
            {error && (
              <div className="mb-8 p-4 bg-red-50 border border-red-200 text-red-700 rounded-lg">
                {error}
              </div>
            )}

            {loading ? (
              <div className="flex flex-col items-center justify-center h-full min-h-[400px] space-y-4">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
                <p className="text-gray-600 animate-pulse">Training models... This may take a few seconds.</p>
              </div>
            ) : result ? (
              <ResultsDashboard result={result} />
            ) : (
              <div className="flex flex-col items-center justify-center h-full min-h-[400px] border-2 border-dashed border-gray-200 rounded-xl bg-white text-gray-600">
                <p>Upload files and click Train to see results</p>
              </div>
            )}
          </div>
        </div>
      </div>
    </main>
  );
}
