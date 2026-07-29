"use client";

import { useMemo, useState } from "react";
import { AnimatePresence, motion } from "framer-motion";
import { ChevronLeft, ChevronRight } from "lucide-react";
import { Button } from "@/components/ui/button";

type VehicleImageCarouselProps = {
  alt: string;
  className?: string;
  images: string[];
};

function uniqueImages(images: string[]) {
  return [...new Set(images.filter((url) => url && url.trim().length > 0))];
}

export function VehicleImageCarousel({ alt, className, images }: VehicleImageCarouselProps) {
  const normalizedImages = useMemo(() => uniqueImages(images), [images]);
  const [index, setIndex] = useState(0);
  const [direction, setDirection] = useState<"next" | "prev">("next");

  if (normalizedImages.length === 0) {
    return null;
  }

  const currentImage = normalizedImages[index] ?? normalizedImages[0];
  const canSlide = normalizedImages.length > 1;

  function nextImage() {
    setDirection("next");
    setIndex((prev) => (prev + 1) % normalizedImages.length);
  }

  function prevImage() {
    setDirection("prev");
    setIndex((prev) => (prev - 1 + normalizedImages.length) % normalizedImages.length);
  }

  return (
    <div className={className}>
      <div className="relative h-full w-full overflow-hidden bg-muted">
        <AnimatePresence initial={false} mode="wait">
          <motion.img
            key={currentImage}
            src={currentImage}
            alt={alt}
            className="h-full w-full object-cover"
            loading="lazy"
            initial={{ opacity: 0, x: direction === "next" ? 24 : -24 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: direction === "next" ? -24 : 24 }}
            transition={{ duration: 0.25, ease: "easeOut" }}
          />
        </AnimatePresence>
        {canSlide ? (
          <>
            <Button
              type="button"
              variant="secondary"
              size="icon"
              className="absolute left-2 top-1/2 h-8 w-8 -translate-y-1/2"
              onClick={prevImage}
              aria-label="Previous image"
            >
              <ChevronLeft className="size-4" />
            </Button>
            <Button
              type="button"
              variant="secondary"
              size="icon"
              className="absolute right-2 top-1/2 h-8 w-8 -translate-y-1/2"
              onClick={nextImage}
              aria-label="Next image"
            >
              <ChevronRight className="size-4" />
            </Button>
            <div className="absolute bottom-2 left-1/2 flex -translate-x-1/2 gap-1 rounded-full bg-background/80 px-2 py-1">
              {normalizedImages.map((_, dotIndex) => (
                <button
                  key={`${alt}-dot-${dotIndex}`}
                  type="button"
                  onClick={() => setIndex(dotIndex)}
                  className={`h-1.5 w-1.5 rounded-full ${dotIndex === index ? "bg-foreground" : "bg-muted-foreground/40"}`}
                  aria-label={`Go to image ${dotIndex + 1}`}
                />
              ))}
            </div>
          </>
        ) : null}
      </div>
    </div>
  );
}
